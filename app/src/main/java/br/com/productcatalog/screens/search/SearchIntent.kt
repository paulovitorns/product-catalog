package br.com.productcatalog.screens.search

import br.com.productcatalog.data.models.SearchResult

sealed class SearchIntent {
    data class SearchProduct(val queryString: String) : SearchIntent()
    data class LoadNextPage(val lastPage: SearchResult) : SearchIntent()
    data class RestoreLastState(val lastViewState: SearchViewState) : SearchIntent()
}
