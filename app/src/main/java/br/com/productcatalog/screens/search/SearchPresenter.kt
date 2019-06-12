package br.com.productcatalog.screens.search

import android.util.Log
import androidx.annotation.VisibleForTesting
import br.com.productcatalog.data.models.ProductResult
import br.com.productcatalog.domain.search.SearchActionComposer
import br.com.productcatalog.domain.search.SearchIntentMapper
import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.library.reactivex.SchedulerProvider
import br.com.productcatalog.library.reactivex.addDisposableTo
import br.com.productcatalog.library.reactivex.applyObservableSchedulers
import br.com.productcatalog.library.state.StateStore
import br.com.productcatalog.screens.BasePresenter
import br.com.productcatalog.screens.BaseUi
import br.com.productcatalog.screens.home.HomeUi
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ActivityScope
class SearchPresenter @Inject constructor(
    private val stateStore: StateStore,
    private val searchActionComposer: SearchActionComposer,
    private val schedulerProvider: SchedulerProvider,
    private val searchIntentMapper: SearchIntentMapper
) : BasePresenter<BaseUi>() {

    // this will help us to test and see if the presenter is receiving and process the first query string correctly
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var lastViewState: SearchViewState? = null

    private val searchUi: SearchUi? get() = baseUi()
    private val publishSubject: PublishSubject<SearchIntent> = PublishSubject.create()

    override fun onCreate() {
        super.onCreate()
        retrieveQueryString()
        bindIntents()
    }

    override fun onSaveState() {
        super.onSaveState()
        // save on memory the SearchViewState
        lastViewState?.let { stateStore.save(SearchUi::class, it) }
    }

    private fun retrieveQueryString() {
        // check if the previous screen has sent a query string to this screen
        val queryString: String? = stateStore.load(HomeUi::class)
        if (!queryString.isNullOrEmpty()) {
            searchUi?.showReceivedQueryString(queryString)
        }
    }

    private fun bindIntents() {

        val searchViewIntent: Observable<SearchIntent> = searchUi?.search()!!
            .debounce(500, TimeUnit.MILLISECONDS, schedulerProvider.workerThread())
            .filter { typed -> typed.isNotEmpty() && typed.length > 1 }
            .map { queryString -> SearchIntent.SearchProduct(queryString) }

        val nextPage: Observable<SearchIntent> = searchUi?.loadNextPage()!!
            .filter {
                lastViewState != null && lastViewState is SearchViewState
            }
            .map { SearchIntent.LoadNextPage(lastViewState?.searchResult!!) }

        val retryIntent: Observable<SearchIntent> = searchUi?.retryButton()!!
            .filter { lastViewState != null && lastViewState is SearchViewState }
            .map { SearchIntent.RestoreLastState(lastViewState!!) }

        val allIntents: Observable<SearchIntent> = Observable.merge(
            searchViewIntent,
            nextPage,
            retryIntent
        )

        val initialState = SearchViewState()

        publishSubject
            .map { searchIntentMapper actionOf it }
            .compose(searchActionComposer.bindActions())
            .compose(applyObservableSchedulers(schedulerProvider))
            .scan(initialState, this::viewStateReducer)
            .subscribe({ result ->
                lastViewState = result
                searchUi?.render(result)
            }, { error ->
                Log.e("SEARCH", error.message)
            }).addDisposableTo(disposeBag)

        allIntents.subscribe(publishSubject)
    }

    private fun viewStateReducer(
        previousState: SearchViewState,
        partialChanges: PartialStateChanged
    ): SearchViewState {
        return when (partialChanges) {
            is PartialStateChanged.Loading -> {
                previousState.nextState { isLoading = true }
            }
            is PartialStateChanged.StateError -> {
                previousState.nextState {
                    isLoading = false
                    stateError = partialChanges.error
                }
            }
            is PartialStateChanged.SearchProductsLoaded -> {
                previousState.nextState {
                    isLoading = false
                    stateError = null
                    isSearchPresentation = true
                    isNextPagePresentation = false
                    searchResult = partialChanges.searchResult
                }
            }
            is PartialStateChanged.NextPageLoaded -> {
                // Retrieve previous products list
                val previousData = previousState.searchResult?.results ?: emptyList<ProductResult>()

                val data = mutableListOf<ProductResult>()
                data.addAll(previousData)
                data.addAll(partialChanges.searchResult.results)

                // Make sure that we'll have a consistency data inside our NextPageLoaded state
                partialChanges.searchResult.results = data

                previousState.nextState {
                    isLoading = false
                    stateError = null
                    isSearchPresentation = false
                    isNextPagePresentation = true
                    searchResult = partialChanges.searchResult
                }
            }
            is PartialStateChanged.LastViewStateRestored -> {
                previousState.nextState {
                    isLoading = false
                    stateError = null
                    isSearchPresentation = true
                    isNextPagePresentation = false
                    searchResult = partialChanges.lastViewState.searchResult
                }
            }
        }
    }
}