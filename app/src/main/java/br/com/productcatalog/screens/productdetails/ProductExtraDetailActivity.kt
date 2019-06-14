package br.com.productcatalog.screens.productdetails

import android.view.MenuItem
import androidx.core.view.isVisible
import br.com.productcatalog.R
import br.com.productcatalog.data.models.Characteristic
import br.com.productcatalog.screens.BaseActivity
import br.com.productcatalog.screens.BaseUi
import br.com.productcatalog.screens.product.CharacteristicAdapter
import kotlinx.android.synthetic.main.extra_details_product_layout.appToolbar
import kotlinx.android.synthetic.main.extra_details_product_layout.characteristicsRecycler
import kotlinx.android.synthetic.main.extra_details_product_layout.description
import kotlinx.android.synthetic.main.extra_details_product_layout.scrollView

interface ProductExtraDetailUi : BaseUi {
    fun showTitle(productTitle: String)
    fun showCharacteristics(characteristics: List<Characteristic>)
    fun showFullDescription(fullDescription: String)
}

class ProductExtraDetailActivity : BaseActivity<ProductExtraDetailPresenter>(), ProductExtraDetailUi {
    override val layoutRes: Int? = R.layout.extra_details_product_layout

    override fun setupToolbar() {
        super.setupToolbar()
        setSupportActionBar(appToolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showTitle(productTitle: String) {
        title = productTitle
    }

    override fun showCharacteristics(characteristics: List<Characteristic>) {
        scrollView.isVisible = false
        characteristicsRecycler.adapter = CharacteristicAdapter(characteristics.toMutableList())
        characteristicsRecycler.isVisible = true
    }

    override fun showFullDescription(fullDescription: String) {
        characteristicsRecycler.isVisible = true
        description.text = fullDescription
        scrollView.isVisible = true
    }
}
