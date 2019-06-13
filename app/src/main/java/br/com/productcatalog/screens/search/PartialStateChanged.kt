package br.com.productcatalog.screens.search

import br.com.productcatalog.data.models.SearchResult

sealed class PartialStateChanged {
    object Loading : PartialStateChanged()
    data class SearchProductsLoaded(val searchResult: SearchResult) : PartialStateChanged()
    data class NextPageLoaded(val searchResult: SearchResult) : PartialStateChanged()
    data class LastViewStateRestored(val lastViewState: SearchViewState) : PartialStateChanged()
    data class ProductDetailOpened(val productId: String) : PartialStateChanged()
    data class StateError(val error: Throwable) : PartialStateChanged()
}
