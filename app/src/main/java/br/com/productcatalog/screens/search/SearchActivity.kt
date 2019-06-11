package br.com.productcatalog.screens.search

import androidx.core.view.isVisible
import br.com.productcatalog.R
import br.com.productcatalog.domain.search.SearchState
import br.com.productcatalog.screens.BaseActivity
import br.com.productcatalog.screens.BaseUi
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.queryTextChanges
import io.reactivex.Observable
import kotlinx.android.synthetic.main.offline_state.retryButton
import kotlinx.android.synthetic.main.search_layout.offlineState
import kotlinx.android.synthetic.main.search_layout.progress
import kotlinx.android.synthetic.main.search_layout.searchView

interface SearchUi : BaseUi {
    fun searchIntent(): Observable<String>
    fun retryConnection(): Observable<Unit>
    fun render(state: SearchState)
}

class SearchActivity : BaseActivity<SearchPresenter>(), SearchUi {

    override val layoutRes: Int? = R.layout.search_layout

    override fun retryConnection(): Observable<Unit> {
        return retryButton.clicks()
    }

    override fun searchIntent(): Observable<String> {
        return searchView.queryTextChanges()
            .map { it.toString() }
    }

    override fun render(state: SearchState) {
        when (state) {
            is SearchState.Idle -> {
                hideProgress()
            }
            is SearchState.Loading -> showProgress()
            is SearchState.SetFirstQueryString -> showStarterQueryString(state.queryString)
            is SearchState.NoConnection -> showOfflineState()
            is SearchState.Online -> hideOfflineState()
        }
    }

    private fun showProgress() {
        progress.isVisible = true
    }

    private fun hideProgress() {
        progress.isVisible = false
    }

    private fun showOfflineState() {
        offlineState.isVisible = true
    }

    private fun hideOfflineState() {
        offlineState.isVisible = false
    }

    private fun showStarterQueryString(query: String) {
        searchView.setQuery(query, false)
    }
}
