package br.com.productcatalog.screens.search

import androidx.annotation.VisibleForTesting
import br.com.productcatalog.domain.network.NetworkConnectionUseCase
import br.com.productcatalog.domain.search.SearchState
import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.library.reactivex.addDisposableTo
import br.com.productcatalog.library.state.StateStore
import br.com.productcatalog.screens.BasePresenter
import br.com.productcatalog.screens.BaseUi
import io.reactivex.Observable
import javax.inject.Inject

@ActivityScope
class SearchPresenter @Inject constructor(
    private val stateStore: StateStore,
    private val networkConnection: NetworkConnectionUseCase
) : BasePresenter<BaseUi>() {

    // this will help us to test and see if the presenter is receiving and process the first query string correctly
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var lastState: SearchState? = null

    private val searchUi: SearchUi? get() = baseUi()
    private lateinit var intentSubject: Observable<SearchState>

    override fun onCreate() {
        super.onCreate()
        // Get the SetFirstQueryString state sent by home activity as default
        lastState = stateStore.load(SearchUi::class)
        bindIntents()
    }

    override fun onSaveState() {
        super.onSaveState()
        lastState?.let { stateStore.save(SearchUi::class, it) }
    }

    private fun bindIntents() {

        val checkNetWorkConnection: Observable<SearchState> = networkConnection.invoke()
            .map { connectionMapper(it) }

        val retryIntent: Observable<SearchState> = searchUi?.retryConnection()!!.switchMap {
            networkConnection.invoke()
        }.map { connectionMapper(it) }

        intentSubject = Observable.merge(checkNetWorkConnection, retryIntent)
        intentSubject.scan(lastState ?: SearchState.Idle, { previousState: SearchState, result: SearchState ->
            reduceMapper(previousState, result)
        }).subscribe({ state ->
            searchUi?.render(state)
        }, { throw IllegalArgumentException(it) }).addDisposableTo(disposeBag)
    }

    private fun connectionMapper(hasConnection: Boolean): SearchState {
        return if (hasConnection) {
            SearchState.Online
        } else {
            SearchState.NoConnection
        }
    }
}

private fun reduceMapper(previousState: SearchState, result: SearchState): SearchState {
    return result
}
