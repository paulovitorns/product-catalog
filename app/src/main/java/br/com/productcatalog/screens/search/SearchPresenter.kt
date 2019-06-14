package br.com.productcatalog.screens.search

import android.util.Log
import androidx.annotation.VisibleForTesting
import br.com.productcatalog.data.models.ProductResult
import br.com.productcatalog.domain.search.AllItemsLoadedException
import br.com.productcatalog.domain.search.SearchActionComposer
import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.library.reactivex.SchedulerProvider
import br.com.productcatalog.library.reactivex.addDisposableTo
import br.com.productcatalog.library.reactivex.applyObservableSchedulers
import br.com.productcatalog.library.state.StateStore
import br.com.productcatalog.screens.BasePresenter
import br.com.productcatalog.screens.BaseUi
import br.com.productcatalog.screens.home.HomeUi
import br.com.productcatalog.screens.product.ProductUi
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ActivityScope
class SearchPresenter @Inject constructor(
    private val stateStore: StateStore,
    private val searchActionComposer: SearchActionComposer,
    private val schedulerProvider: SchedulerProvider
) : BasePresenter<BaseUi>() {

    // this will help us to test and see if the presenter is receiving and process the first query string correctly
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var lastViewState: SearchViewState? = null

    private val searchUi: SearchUi? get() = baseUi()
    private val publishSubject: PublishSubject<SearchViewAction> = PublishSubject.create()

    override fun onCreate() {
        super.onCreate()
        retrieveQueryString()
        setupPublisher()
        bindIntents()
        restoreStateIfNeeded()
    }

    override fun onSaveState() {
        super.onSaveState()
        // save on memory the SearchViewState
        lastViewState?.let { stateStore.save(SearchUi::class, it) }
    }

    private fun restoreStateIfNeeded() {
        val lastState: SearchViewState? = stateStore.load(SearchUi::class)
        if (lastState != null) {
            publishSubject.onNext(SearchViewAction.RestoreLastState(lastState))
        }
    }

    private fun retrieveQueryString() {
        // check if the previous screen has sent a query string to this screen
        val queryString: String? = stateStore.load(HomeUi::class)
        if (!queryString.isNullOrEmpty()) {
            searchUi?.showReceivedQueryString(queryString)
        }
    }

    private fun setupPublisher() {

        val initialState = SearchViewState()

        publishSubject
            .compose(searchActionComposer.bindActions())
            .compose(applyObservableSchedulers(schedulerProvider))
            .scan(initialState, this::viewStateReducer)
            .distinctUntilChanged()
            .subscribe({ result ->
                lastViewState = result

                if (!result.productId.isNullOrBlank()) {
                    stateStore.save(ProductUi::class, result.productId)
                }

                searchUi?.render(result)
            }, { error ->
                Log.e("SEARCH", error.message)
            }).addDisposableTo(disposeBag)
    }

    private fun bindIntents() {

        val openProductIntent: Observable<SearchViewAction> = searchUi?.productSelected()!!
            .map { SearchViewAction.OpenProductDetail(it.id) }

        val searchViewIntent: Observable<SearchViewAction> = searchUi?.search()!!
            .debounce(500, TimeUnit.MILLISECONDS, schedulerProvider.workerThread())
            .filter { it.trim().isNotEmpty() }
            .filter { typed -> typed != lastViewState?.searchResult?.query }
            .map { queryString -> SearchViewAction.SearchProduct(queryString) }

        val nextPage: Observable<SearchViewAction> = searchUi?.loadNextPage()!!
            .filter { lastViewState != null }
            .filter { lastViewState is SearchViewState }
            .filter { lastViewState?.hasLoadedAllPages == false }
            .map { SearchViewAction.LoadNextPage(lastViewState?.searchResult!!) }

        val retryIntent: Observable<SearchViewAction> = searchUi?.retryButton()!!
            .filter { lastViewState != null && lastViewState is SearchViewState }
            .map { SearchViewAction.RestoreLastState(lastViewState!!) }

        val allIntents: Observable<SearchViewAction> = Observable.merge(
            searchViewIntent,
            nextPage,
            retryIntent,
            openProductIntent
        )

        allIntents.subscribe(publishSubject)
    }

    private fun viewStateReducer(previousState: SearchViewState, partialChanges: PartialStateChanged): SearchViewState {
        return when (partialChanges) {
            is PartialStateChanged.Loading -> {
                previousState.builder()
                    .setStateError(null)
                    .setLoading(true)
                    .build()
            }
            is PartialStateChanged.StateError -> {
                previousState.builder()
                    .setLoading(false)
                    .setLoadedAllPages(partialChanges.error is AllItemsLoadedException)
                    .setStateError(partialChanges.error)
                    .build()
            }
            is PartialStateChanged.SearchProductsLoaded -> {
                previousState.builder()
                    .setLoading(false)
                    .setSearchPresentation(true)
                    .setSearchResult(partialChanges.searchResult)
                    .setStateError(null)
                    .build()
            }
            is PartialStateChanged.NextPageLoaded -> {
                // Retrieve previous products list
                val previousData = previousState.searchResult?.results ?: emptyList<ProductResult>()

                val data = mutableListOf<ProductResult>()
                data.addAll(previousData)
                data.addAll(partialChanges.searchResult.results)

                // Make sure that we'll have a consistency data inside our NextPageLoaded state
                partialChanges.searchResult.results = data

                previousState.builder()
                    .setLoading(false)
                    .setStateError(null)
                    .setSearchPresentation(false)
                    .setNextPagePresentation(true)
                    .setSearchResult(partialChanges.searchResult)
                    .build()
            }
            is PartialStateChanged.LastViewStateRestored -> {
                previousState.builder()
                    .setLoading(false)
                    .setSearchPresentation(true)
                    .setNextPagePresentation(false)
                    .setLoadedAllPages(partialChanges.lastViewState.hasLoadedAllPages)
                    .setSearchResult(partialChanges.lastViewState.searchResult)
                    .setStateError(null)
                    .build()
            }
            is PartialStateChanged.ProductDetailOpened -> {
                previousState.builder()
                    .setLoading(false)
                    .setSearchPresentation(false)
                    .setNextPagePresentation(false)
                    .setStateError(null)
                    .setProductId(partialChanges.productId)
                    .build()
            }
        }
    }
}
