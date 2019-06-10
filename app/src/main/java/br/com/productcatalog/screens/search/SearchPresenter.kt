package br.com.productcatalog.screens.search

import androidx.annotation.VisibleForTesting
import br.com.productcatalog.domain.network.NetworkConnectionUseCase
import br.com.productcatalog.domain.search.SearchState
import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.library.reactivex.addDisposableTo
import br.com.productcatalog.library.state.StateStore
import br.com.productcatalog.screens.BasePresenter
import br.com.productcatalog.screens.BaseUi
import javax.inject.Inject

@ActivityScope
class SearchPresenter @Inject constructor(
    private val stateStore: StateStore,
    private val networkConnectionUseCase: NetworkConnectionUseCase
) : BasePresenter<BaseUi>() {

    // this will help us to test and see if the presenter is receiving and process the first query string correctly
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var lastState: SearchState? = null

    private val searchUi: SearchUi? get() = baseUi()

    override fun onCreate() {
        super.onCreate()
        lastState = stateStore.load(SearchUi::class)

        networkConnectionUseCase()
            .subscribe({
                bindIntents()
            }, {
                lastState = SearchState.NoNetworkConnection
                bindIntents()
            }).addDisposableTo(disposeBag)
    }

    override fun onSaveState() {
        super.onSaveState()
        lastState?.let { stateStore.save(SearchUi::class, it) }
    }

    fun bindIntents() {
        lastState?.let { searchUi?.render(it) }
    }
}
