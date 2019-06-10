package br.com.productcatalog.screens.search

import br.com.productcatalog.R
import br.com.productcatalog.domain.search.SearchState
import br.com.productcatalog.screens.BaseActivity
import br.com.productcatalog.screens.BaseUi
import kotlinx.android.synthetic.main.search_layout.searchView

interface SearchUi : BaseUi {
    fun render(searchState: SearchState)
}

class SearchActivity : BaseActivity<SearchPresenter>(), SearchUi {

    override val layoutRes: Int? = R.layout.search_layout

    override fun render(searchState: SearchState) {
        when (searchState) {
            is SearchState.LoadingProducts -> {
            }
            is SearchState.NoNetworkConnection -> {
            }
            is SearchState.ApiError -> {
            }
            is SearchState.ProductNotFound -> {
            }
            is SearchState.StarterSearch -> showStarterQueryString(searchState.query)
            is SearchState.PerformSearch -> {
            }
        }
    }

    private fun showStarterQueryString(query: String) {
        searchView.setQuery(query, false)
    }
}
