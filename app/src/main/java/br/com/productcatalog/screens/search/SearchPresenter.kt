package br.com.productcatalog.screens.search

import androidx.annotation.VisibleForTesting
import br.com.productcatalog.data.models.SearchResult
import br.com.productcatalog.data.search.NoResultFoundException
import br.com.productcatalog.domain.network.NetworkConnectionUseCase
import br.com.productcatalog.domain.search.AllItemsLoadedException
import br.com.productcatalog.domain.search.MlbSearchProductUseCase
import br.com.productcatalog.domain.search.PaginateResultUseCase
import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.library.reactivex.SchedulerProvider
import br.com.productcatalog.library.reactivex.addDisposableTo
import br.com.productcatalog.library.state.StateStore
import br.com.productcatalog.screens.BasePresenter
import br.com.productcatalog.screens.BaseUi
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ActivityScope
class SearchPresenter @Inject constructor(
    private val stateStore: StateStore,
    private val networkConnection: NetworkConnectionUseCase,
    private val mlbSearchProduct: MlbSearchProductUseCase,
    private val paginateResult: PaginateResultUseCase,
    private val schedulerProvider: SchedulerProvider
) : BasePresenter<BaseUi>() {

    // this will help us to test and see if the presenter is receiving and process the first query string correctly
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var lastViewState: SearchViewState? = null

    private val searchUi: SearchUi? get() = baseUi()
    private lateinit var intentSubject: Observable<SearchViewState>

    override fun onCreate() {
        super.onCreate()
        lastViewState = stateStore.load(SearchUi::class)
        lastViewState?.let { validateFirstState() }
        bindIntents()
    }

    override fun onSaveState() {
        super.onSaveState()
        lastViewState?.let { stateStore.save(SearchUi::class, it) }
    }

    private fun validateFirstState() {
        // check if the first state comes from the previously screen
        if (lastViewState is SearchViewState.SetFirstQueryString) {
            searchUi?.showReceivedQueryString((lastViewState as SearchViewState.SetFirstQueryString).queryString)
        }
    }

    private fun bindIntents() {

        // Check the connection before start a new call
        val checkNetWorkConnection: Observable<SearchViewState> = networkConnection.invoke()
            .map { connectionMapper(it) }

        // Bind the SearchUi::search events
        val searchViewIntent: Observable<SearchViewState> = searchUi?.search()!!
            .debounce(500, TimeUnit.MILLISECONDS, schedulerProvider.workerThread())
            .filter { typed -> typed.isNotEmpty() && typed.length > 1 }
            .switchMap { queryString -> mlbSearchProduct(queryString) }
            .map(this::mapSearchSuccess)
            .startWith(SearchViewState.FirstPageLoading)
            .onErrorReturn(this::mapSearchResponseError)

        val nextPage: Observable<SearchViewState> = searchUi?.loadNextPage()!!
            .switchMap { lastSearchResult -> paginateResult(lastSearchResult) }
            .map(this::mapNextPageSuccess)
            .startWith(SearchViewState.NextPageLoading)
            .onErrorReturn(this::mapSearchResponseError)

        // Bind the SearchUi::retryConnection events
        val retryIntent: Observable<SearchViewState> = searchUi?.retryConnection()!!.switchMap {
            networkConnection()
        }.map { connectionMapper(it) }

        intentSubject = Observable.merge(checkNetWorkConnection, searchViewIntent, nextPage, retryIntent)
        intentSubject.scan(
            lastViewState ?: SearchViewState.Idle,
            { previousViewState: SearchViewState, result: SearchViewState ->
                reduceMapper(previousViewState, result)
            }).subscribe({
            searchUi?.render(it)
        }, { throw IllegalArgumentException(it) }).addDisposableTo(disposeBag)
    }

    private fun mapNextPageSuccess(result: SearchResult): SearchViewState {
        return SearchViewState.ShowProductNextPage(result)
    }

    private fun mapSearchSuccess(result: SearchResult): SearchViewState {
        return SearchViewState.ShowProductResult(result)
    }

    private fun mapSearchResponseError(error: Throwable): SearchViewState {
        return when (error) {
            is NoResultFoundException -> SearchViewState.ShowErrorSearchView(error.queryString)
            is AllItemsLoadedException -> SearchViewState.ShowAllItemsLoaded
            else -> SearchViewState.ShowDefaultError
        }
    }

    private fun connectionMapper(hasConnection: Boolean): SearchViewState {
        return if (hasConnection) {
            SearchViewState.Online
        } else {
            SearchViewState.NoConnection
        }
    }
}

private fun reduceMapper(previousViewState: SearchViewState, result: SearchViewState): SearchViewState {
    return result
}
