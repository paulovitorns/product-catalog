package br.com.productcatalog.screens.home

import br.com.productcatalog.domain.CleanQueryStringUseCase
import br.com.productcatalog.screens.BasePresenter
import br.com.productcatalog.screens.BaseUi
import javax.inject.Inject

class HomePresenter @Inject constructor(
    private val cleanQueryStringUseCase: CleanQueryStringUseCase
) : BasePresenter<BaseUi>() {

    private val homeUi: HomeUi? get() = baseUi()

    fun onSendSearch(query: String) {
        val cleanQueryString = cleanQueryStringUseCase(query)
        homeUi?.openSearchScreen(cleanQueryString)
    }
}
