package br.com.productcatalog.domain.search

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class SetFirstQueryString(val queryString: String) : SearchState()
    object NoConnection : SearchState()
    object Online : SearchState()
}
