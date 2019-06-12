package br.com.productcatalog.screens.search

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.productcatalog.R
import br.com.productcatalog.data.models.SearchResult
import br.com.productcatalog.library.extension.hideKeyboard
import br.com.productcatalog.library.extension.toast
import br.com.productcatalog.screens.BaseActivity
import br.com.productcatalog.screens.BaseUi
import com.jakewharton.rxbinding3.recyclerview.scrollStateChanges
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.queryTextChanges
import io.reactivex.Observable
import kotlinx.android.synthetic.main.offline_state.retryButton
import kotlinx.android.synthetic.main.search_layout.defaultError
import kotlinx.android.synthetic.main.search_layout.offlineState
import kotlinx.android.synthetic.main.search_layout.progress
import kotlinx.android.synthetic.main.search_layout.recyclerView
import kotlinx.android.synthetic.main.search_layout.searchNotFound
import kotlinx.android.synthetic.main.search_layout.searchView
import kotlinx.android.synthetic.main.search_not_found_state.notFoundDescription

interface SearchUi : BaseUi {

    fun showReceivedQueryString(queryString: String)
    fun search(): Observable<String>
    fun loadNextPage(): Observable<SearchResult>
    fun retryConnection(): Observable<Unit>

    /**
     * This method will render on the screen the [viewState] sent by the presentation class
     *
     * @param viewState is a state that represents a return of an
     * intention performed by the user or initial states
     */
    fun render(viewState: SearchViewState)
}

class SearchActivity : BaseActivity<SearchPresenter>(), SearchUi {

    override val layoutRes: Int? = R.layout.search_layout
    private val linearLayoutManager by lazy { LinearLayoutManager(this) }
    private val searchAdapter by lazy { SearchAdapter() }
    private lateinit var searchResult: SearchResult

    override fun setupViews() {
        super.setupViews()
        with(recyclerView) {
            layoutManager = linearLayoutManager
            adapter = searchAdapter
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    hideKeyboard()
                    searchView.clearFocus()
                }
            }
        })
    }

    override fun showReceivedQueryString(queryString: String) {
        searchView.setQuery(queryString, false)
    }

    override fun search(): Observable<String> {
        return searchView.queryTextChanges()
            .map { it.toString() }
    }

    override fun loadNextPage(): Observable<SearchResult> {
        return recyclerView.scrollStateChanges()
            .filter { ::searchResult.isInitialized }
            .filter { event -> event == RecyclerView.SCROLL_STATE_IDLE }
            .filter {
                linearLayoutManager.findLastCompletelyVisibleItemPosition() == searchAdapter.itemCount - 1
            }.map { searchResult }
    }

    override fun retryConnection(): Observable<Unit> {
        return retryButton.clicks()
    }

    override fun render(viewState: SearchViewState) {
        when (viewState) {
            is SearchViewState.Idle -> hideProgress()
            is SearchViewState.SetFirstQueryString -> showStarterQueryString(viewState.queryString)
            is SearchViewState.Online -> hideOfflineState()
            is SearchViewState.FirstPageLoading,
            is SearchViewState.NextPageLoading -> showProgress()
            is SearchViewState.ShowProductResult -> showSearchResult(viewState.result)
            is SearchViewState.ShowProductNextPage -> showNextPage(viewState.result)
            is SearchViewState.NoConnection -> showOfflineState()
            is SearchViewState.ShowAllItemsLoaded -> showAllItemsLoaded()
            is SearchViewState.ShowErrorSearchView -> showSearchError(viewState.queryString)
            is SearchViewState.ShowDefaultError -> showDefaultError()
        }
    }

    private fun showSearchError(queryString: String) {
        searchAdapter.clearAll()
        searchNotFound.isVisible = true

        val descriptionFormatted = notFoundDescription.text.toString().format(queryString)

        // This will set the queryString to bold
        val spannableString = SpannableString(descriptionFormatted).apply {
            setSpan(
                StyleSpan(Typeface.BOLD),
                descriptionFormatted.length - queryString.length,
                descriptionFormatted.length,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
        }

        notFoundDescription.text = spannableString
        hideProgress()
    }

    private fun showDefaultError() {
        searchAdapter.clearAll()
        defaultError.isVisible = true
        hideProgress()
    }

    private fun showAllItemsLoaded() {
        toast(R.string.all_items_loaded)
        hideProgress()
    }

    private fun showSearchResult(searchResult: SearchResult) {
        searchNotFound.isVisible = false

        this.searchResult = searchResult
        searchAdapter.setItems(searchResult.results)
        hideProgress()
    }

    private fun showNextPage(searchResult: SearchResult) {
        searchNotFound.isVisible = false

        this.searchResult = searchResult
        searchResult.results.forEach { item ->
            searchAdapter.addItem(item)
        }
        hideProgress()
    }

    private fun showProgress() {
        progress.isVisible = true
    }

    private fun hideProgress() {
        progress.isVisible = false
    }

    private fun showOfflineState() {
        offlineState.isVisible = true
        hideProgress()
    }

    private fun hideOfflineState() {
        offlineState.isVisible = false
    }

    private fun showStarterQueryString(query: String) {
        searchView.setQuery(query, false)
    }
}
