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
import br.com.productcatalog.data.search.NoResultFoundException
import br.com.productcatalog.domain.search.AllItemsLoadedException
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
import java.net.UnknownHostException

interface SearchUi : BaseUi {
    fun showReceivedQueryString(queryString: String)
    fun search(): Observable<String>
    fun loadNextPage(): Observable<Boolean>
    fun retryButton(): Observable<Unit>

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
    private val searchAdapter by lazy { SearchAdapter(this) }

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

    override fun loadNextPage(): Observable<Boolean> {
        return recyclerView.scrollStateChanges()
            .filter { event -> event == RecyclerView.SCROLL_STATE_IDLE }
            .filter {
                linearLayoutManager.findLastCompletelyVisibleItemPosition() == searchAdapter.itemCount - 1
            }.map { true }
    }

    override fun retryButton(): Observable<Unit> {
        return retryButton.clicks()
    }

    override fun render(viewState: SearchViewState) {
        hideAllErrorsState()
        if (viewState.isLoading) {
            showProgress()
            return
        }

        if (viewState.stateError != null) {
            when (viewState.stateError) {
                is NoResultFoundException -> {
                    showSearchError((viewState.stateError as NoResultFoundException).queryString)
                }
                is AllItemsLoadedException -> showAllItemsLoaded()
                is UnknownHostException -> showOfflineState()
                else -> showDefaultError()
            }
            return
        }

        viewState.searchResult.takeIf { viewState.isSearchPresentation && viewState.stateError == null }?.let {
            hideProgress()
            showSearchResult(it)
            return
        }

        viewState.searchResult.takeIf { viewState.isNextPagePresentation && viewState.stateError == null }?.let {
            hideProgress()
            showNextPage(it)
            return
        }
    }

    private fun hideAllErrorsState() {
        searchNotFound.isVisible = false
        defaultError.isVisible = false
        hideOfflineState()
    }

    private fun showSearchError(queryString: String) {
        hideRecycler()

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
        searchNotFound.isVisible = true
        hideProgress()
    }

    private fun showDefaultError() {
        hideRecycler()
        defaultError.isVisible = true
        hideProgress()
    }

    private fun showAllItemsLoaded() {
        toast(R.string.all_items_loaded)
        hideProgress()
    }

    private fun hideRecycler() {
        recyclerView.isVisible = false
        searchAdapter.clearAll()
    }

    private fun showSearchResult(searchResult: SearchResult) {
        recyclerView.isVisible = true
        searchAdapter.setItem(searchResult.results)
        hideProgress()
    }

    private fun showNextPage(searchResult: SearchResult) {
        searchAdapter.addItems(searchResult.results)
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
}
