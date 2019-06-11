package br.com.productcatalog.screens.home

import br.com.productcatalog.domain.CleanQueryStringUseCase
import br.com.productcatalog.domain.search.SearchState
import br.com.productcatalog.library.state.StateStore
import br.com.productcatalog.screens.BasePresenter
import br.com.productcatalog.screens.BaseUi
import br.com.productcatalog.screens.search.SearchUi
import javax.inject.Inject

class HomePresenter @Inject constructor(
    private val stateStore: StateStore,
    private val cleanQueryStringUseCase: CleanQueryStringUseCase

) : BasePresenter<BaseUi>() {

    private val homeUi: HomeUi? get() = baseUi()

    fun onSendSearch(query: String) {
        // filter extras spaces of the query string
        val cleanQueryString = cleanQueryStringUseCase(query)

        // store a new state to send to the search screen
        stateStore.save(SearchUi::class, SearchState.SetFirstQueryString(cleanQueryString))

        homeUi?.openSearchScreen(cleanQueryString)
    }
}
