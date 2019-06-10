package br.com.productcatalog.domain.search

sealed class SearchState {
    object LoadingProducts : SearchState()
    object NoNetworkConnection : SearchState()
    object ApiError : SearchState()
    object ProductNotFound : SearchState()
    data class StarterSearch(val query: String) : SearchState()
    data class PerformSearch(val query: String) : SearchState()
}
