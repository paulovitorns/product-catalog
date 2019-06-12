package br.com.productcatalog.domain.search

import br.com.productcatalog.data.models.SearchResult
import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.screens.search.PartialStateChanged
import br.com.productcatalog.screens.search.PartialStateChanged.LastViewStateRestored
import br.com.productcatalog.screens.search.PartialStateChanged.NextPageLoaded
import br.com.productcatalog.screens.search.PartialStateChanged.SearchProductsLoaded
import br.com.productcatalog.screens.search.PartialStateChanged.StateError
import br.com.productcatalog.screens.search.SearchViewAction
import br.com.productcatalog.screens.search.SearchViewAction.LoadNextPage
import br.com.productcatalog.screens.search.SearchViewAction.RestoreLastState
import br.com.productcatalog.screens.search.SearchViewAction.SearchProduct
import javax.inject.Inject

@ActivityScope
class SearchResultMapper @Inject constructor() {

    fun <T> stateOf(action: SearchViewAction, result: T): PartialStateChanged {
        return when (action) {
            is SearchProduct -> SearchProductsLoaded(result as SearchResult)
            is LoadNextPage -> NextPageLoaded(result as SearchResult)
            is RestoreLastState -> LastViewStateRestored(action.lastViewState)
        }
    }

    fun errorOf(error: Throwable): PartialStateChanged {
        return StateError(error)
    }
}
