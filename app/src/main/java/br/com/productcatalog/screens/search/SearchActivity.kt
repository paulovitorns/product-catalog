package br.com.productcatalog.screens.search

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.productcatalog.R
import br.com.productcatalog.data.models.ProductResult
import br.com.productcatalog.data.models.SearchResult
import br.com.productcatalog.data.search.NoResultFoundException
import br.com.productcatalog.domain.search.AllItemsLoadedException
import br.com.productcatalog.library.extension.hideKeyboard
import br.com.productcatalog.library.extension.toast
import br.com.productcatalog.screens.BaseActivity
import br.com.productcatalog.screens.BaseUi
import br.com.productcatalog.screens.product.ProductActivity
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
    fun productSelected(): Observable<ProductResult>
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
    private var lastRecyclerPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            lastRecyclerPosition = savedInstanceState.getInt(LAST_RECYCLER_POSITION)
        }
    }

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

    override fun onSaveInstanceState(outState: Bundle?) {
        val linearManager = recyclerView.layoutManager as LinearLayoutManager
        val scrollPosition = linearManager.findFirstVisibleItemPosition()
        outState?.putInt(LAST_RECYCLER_POSITION, scrollPosition)
        super.onSaveInstanceState(outState)
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

    override fun productSelected(): Observable<ProductResult> {
        return searchAdapter.onItemSelected()
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
                    showSearchError(viewState.stateError.queryString)
                }
                is AllItemsLoadedException -> showAllItemsLoaded()
                is UnknownHostException -> showOfflineState()
                else -> showDefaultError()
            }
            return
        }

        viewState.searchResult.takeIf { viewState.isSearchPresentation }?.let {
            hideProgress()
            showSearchResult(it)
            return
        }

        viewState.searchResult.takeIf { viewState.isNextPagePresentation }?.let {
            hideProgress()
            showNextPage(it)
            return
        }

        if (!viewState.productId.isNullOrBlank()) {
            openProductScreen()
        }
    }

    private fun openProductScreen() {
        Intent(this, ProductActivity::class.java).also {
            startActivity(it)
            hideProgress()
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
        searchAdapter.setItems(searchResult.results)

        if (lastRecyclerPosition > 0) {
            val linearManager = recyclerView.layoutManager as LinearLayoutManager
            linearManager.scrollToPositionWithOffset(lastRecyclerPosition, 0)
        }

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
        hideRecycler()

        offlineState.isVisible = true
        hideProgress()
    }

    private fun hideOfflineState() {
        offlineState.isVisible = false
    }
}

private const val LAST_RECYCLER_POSITION = "last_recycler_position"
