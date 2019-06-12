package br.com.productcatalog.screens.search

import br.com.productcatalog.data.models.SearchResult

sealed class SearchViewState {
    object Idle : SearchViewState()
    data class SetFirstQueryString(val queryString: String) : SearchViewState()
    object Online : SearchViewState()
    object FirstPageLoading : SearchViewState()
    data class ShowProductResult(val result: SearchResult) : SearchViewState()
    object NextPageLoading : SearchViewState()
    data class ShowProductNextPage(val result: SearchResult) : SearchViewState()
    object NoConnection : SearchViewState()
    data class ShowErrorSearchView(val queryString: String) : SearchViewState()
    object ShowAllItemsLoaded : SearchViewState()
    object ShowDefaultError : SearchViewState()
}
