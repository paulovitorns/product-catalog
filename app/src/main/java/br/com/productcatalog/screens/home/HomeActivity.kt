package br.com.productcatalog.screens.home

import android.content.Intent
import android.widget.SearchView
import br.com.productcatalog.R
import br.com.productcatalog.screens.BaseActivity
import br.com.productcatalog.screens.BaseUi
import br.com.productcatalog.screens.search.SearchActivity
import kotlinx.android.synthetic.main.home_layout.homeSearchView

interface HomeUi : BaseUi {
    fun openSearchScreen(query: String)
}

class HomeActivity : BaseActivity<HomePresenter>(), HomeUi {

    override val layoutRes: Int? = R.layout.home_layout

    override fun setupViews() {
        super.setupViews()

        homeSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { presenter.onSendSearch(it) }
                return false
            }

            override fun onQueryTextChange(lastText: String) = false
        })
    }

    override fun openSearchScreen(query: String) {
        Intent(this, SearchActivity::class.java).also {
            startActivity(it)
        }
    }
}
