package br.com.productcatalog.screens.product

import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.transition.TransitionManager
import br.com.productcatalog.R
import br.com.productcatalog.data.models.Characteristic
import br.com.productcatalog.data.models.ProductDescription
import br.com.productcatalog.data.models.ProductDetail
import br.com.productcatalog.library.extension.toMoney
import br.com.productcatalog.screens.BaseActivity
import br.com.productcatalog.screens.BaseUi
import kotlinx.android.synthetic.main.product_layout.appToolbar
import kotlinx.android.synthetic.main.product_layout.characteristics
import kotlinx.android.synthetic.main.product_layout.characteristicsDivider
import kotlinx.android.synthetic.main.product_layout.characteristicsRecycler
import kotlinx.android.synthetic.main.product_layout.description
import kotlinx.android.synthetic.main.product_layout.descriptionDivider
import kotlinx.android.synthetic.main.product_layout.descriptionEndDivider
import kotlinx.android.synthetic.main.product_layout.descriptionTitle
import kotlinx.android.synthetic.main.product_layout.detailsContainer
import kotlinx.android.synthetic.main.product_layout.gradientDescription
import kotlinx.android.synthetic.main.product_layout.photosSize
import kotlinx.android.synthetic.main.product_layout.price
import kotlinx.android.synthetic.main.product_layout.productTitle
import kotlinx.android.synthetic.main.product_layout.progress
import kotlinx.android.synthetic.main.product_layout.statusProduct

interface ProductUi : BaseUi {

    fun render(state: ProductViewState)
}

class ProductActivity : BaseActivity<ProductPresenter>(), ProductUi {

    override val layoutRes: Int? = R.layout.product_layout

    override fun setupToolbar() {
        super.setupToolbar()
        setSupportActionBar(appToolbar)
        title = ""
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

    override fun render(state: ProductViewState) {
        if (state.isLoading) {
            showProgress()
            return
        }

        state.productDetail?.takeIf { state.isProductPresentation }?.let {
            hideProgress()
            with(state.productDetail) {
                showPhotoGallery(this)
                showProductDetails(this)
                showCharacteristics(this)
            }
        }

        state.productDescription?.takeIf { state.isDescriptionPresentation }?.let {
            hideProgress()
            showProductDescription(state.productDescription)
        }
    }

    private fun showPhotoGallery(productDetail: ProductDetail) {
        if (productDetail.pictures.isNotEmpty()) {
            val format = getString(R.string.num_photos)
            photosSize.text = format.format(productDetail.pictures.size)
            photosSize.isVisible = true
        }
    }

    private fun showProductDetails(productDetail: ProductDetail) {
        productTitle.text = productDetail.title
        price.text = productDetail.price.toMoney(productDetail.currencyId)

        val formatStatus = getString(R.string.products_status)
        val productCondition = if (productDetail.condition.toLowerCase() == "new") {
            getString(R.string.new_product)
        } else {
            getString(R.string.used_product)
        }
        statusProduct.text = formatStatus.format(productCondition, productDetail.soldQuantity)
    }

    private fun showCharacteristics(productDetail: ProductDetail) {

        fun List<Characteristic>.getTopFourItems(): MutableList<Characteristic> {
            return this.take(4).reversed().toMutableList()
        }

        if (productDetail.characteristics?.isEmpty() == true) {
            return
        }

        characteristicsRecycler.adapter = CharacteristicAdapter(productDetail.characteristics!!.getTopFourItems())

        characteristicsDivider.isVisible = true
        characteristics.isVisible = true
        characteristicsRecycler.isVisible = true
        TransitionManager.beginDelayedTransition(detailsContainer)
    }

    private fun showProductDescription(productDescription: ProductDescription) {
        if (productDescription.text.isNotEmpty()) {
            description.text = productDescription.text
        } else {
            description.text = productDescription.plainText
        }

        descriptionDivider.isVisible = true
        descriptionTitle.isVisible = true
        description.isVisible = true
        gradientDescription.isVisible = true
        descriptionEndDivider.isVisible = true
        TransitionManager.beginDelayedTransition(detailsContainer)
    }

    private fun showProgress() {
        progress.isVisible = true
    }

    private fun hideProgress() {
        progress.isVisible = false
    }
}
