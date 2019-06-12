package br.com.productcatalog.screens.search

import br.com.productcatalog.data.models.SearchResult

data class SearchViewState(
    // indicates that's there's something working
    var isLoading: Boolean = false,
    // indicates that's there's a fresh search to show
    var isSearchPresentation: Boolean = false,
    // indicates that's there's a new page to show
    var isNextPagePresentation: Boolean = false,
    // indicates that's has loaded all the pages
    var hasLoadedAllPages: Boolean = false,
    // used to store data result from search products and load next page products
    var searchResult: SearchResult? = null,
    // indicates that's occurs some error while loading the next page
    var stateError: Throwable? = null
)

fun SearchViewState.nextState(data: SearchViewState.() -> Unit): SearchViewState {
    return this.apply(data)
}

sealed class SearchViewAction {
    data class SearchProduct(val queryString: String) : SearchViewAction()
    data class LoadNextPage(val lastPage: SearchResult) : SearchViewAction()
    data class RestoreLastState(val lastViewState: SearchViewState) : SearchViewAction()
}
