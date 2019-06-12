package br.com.productcatalog.domain.search

import br.com.productcatalog.screens.search.SearchIntent
import br.com.productcatalog.screens.search.SearchViewAction
import javax.inject.Inject

class SearchIntentMapper @Inject constructor() {

    infix fun actionOf(intent: SearchIntent): SearchViewAction {
        return when (intent) {
            is SearchIntent.SearchProduct -> SearchViewAction.SearchProduct(intent.queryString)
            is SearchIntent.LoadNextPage -> SearchViewAction.LoadNextPage(intent.lastPage)
            is SearchIntent.RestoreLastState -> SearchViewAction.RestoreLastState(intent.lastViewState)
        }
    }
}
