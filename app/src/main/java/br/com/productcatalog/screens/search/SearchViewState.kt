package br.com.productcatalog.screens.search

import br.com.productcatalog.data.models.ProductResult
import br.com.productcatalog.data.models.SearchResult

data class SearchViewState(
    // stored the first query string to be used in case of lost connection before load the first page
    val lastQueryString: String? = null,
    // indicates that's there's something working
    val isLoading: Boolean = false,
    // indicates that's there's a fresh search to show
    val isSearchPresentation: Boolean = false,
    // indicates that's there's a new page to show
    val isNextPagePresentation: Boolean = false,
    // indicates that's has loaded all the pages
    val hasLoadedAllPages: Boolean = false,
    // used to store data result from search products and load next page products
    val searchResult: SearchResult? = null,
    val productSelected: ProductResult? = null,
    // indicates that's occurs some error while loading the next page
    val stateError: Throwable? = null
) {

    fun builder(): Builder {
        return Builder(this)
    }

    class Builder(searchViewState: SearchViewState) {
        private var lastQueryString: String? = searchViewState.lastQueryString
        private var isLoading = searchViewState.isLoading
        private var isSearchPresentation = searchViewState.isSearchPresentation
        private var isNextPagePresentation = searchViewState.isNextPagePresentation
        private var hasLoadedAllPages = searchViewState.hasLoadedAllPages
        private var searchResult: SearchResult? = searchViewState.searchResult
        private var productSelected: ProductResult? = searchViewState.productSelected
        private var stateError: Throwable? = searchViewState.stateError

        fun setLastQueryString(queryString: String?): Builder {
            this.lastQueryString = queryString
            return this
        }

        fun setLoading(flag: Boolean): Builder {
            isLoading = flag
            return this
        }

        fun setSearchPresentation(flag: Boolean): Builder {
            this.isSearchPresentation = flag
            return this
        }

        fun setNextPagePresentation(flag: Boolean): Builder {
            this.isNextPagePresentation = flag
            return this
        }

        fun setLoadedAllPages(flag: Boolean): Builder {
            this.hasLoadedAllPages = flag
            return this
        }

        fun setSearchResult(searchResult: SearchResult?): Builder {
            this.searchResult = searchResult
            return this
        }

        fun setProductSelected(productSelected: ProductResult?): Builder {
            this.productSelected = productSelected
            return this
        }

        fun setStateError(error: Throwable?): Builder {
            this.stateError = error
            return this
        }

        fun build(): SearchViewState {
            return SearchViewState(
                lastQueryString,
                isLoading,
                isSearchPresentation,
                isNextPagePresentation,
                hasLoadedAllPages,
                searchResult,
                productSelected,
                stateError
            )
        }
    }
}

sealed class SearchViewAction {
    data class SearchProduct(val queryString: String) : SearchViewAction()
    data class LoadNextPage(val lastPage: SearchResult) : SearchViewAction()
    data class RestoreLastState(val lastViewState: SearchViewState) : SearchViewAction()
    data class OpenProductDetail(val productResult: ProductResult) : SearchViewAction()
}
