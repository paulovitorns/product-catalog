package br.com.productcatalog.screens.product

import android.content.Intent
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.MenuItem
import androidx.core.view.isVisible
import br.com.productcatalog.R
import br.com.productcatalog.data.models.Characteristic
import br.com.productcatalog.data.models.ProductDescription
import br.com.productcatalog.data.models.ProductDetail
import br.com.productcatalog.data.search.NoResultFoundException
import br.com.productcatalog.library.extension.toMoney
import br.com.productcatalog.screens.BaseActivity
import br.com.productcatalog.screens.BaseUi
import br.com.productcatalog.screens.productdetails.ProductExtraDetailActivity
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.default_error_state.defaultErrorDescription
import kotlinx.android.synthetic.main.offline_state.retryButton
import kotlinx.android.synthetic.main.product_layout.appToolbar
import kotlinx.android.synthetic.main.product_layout.characteristics
import kotlinx.android.synthetic.main.product_layout.characteristicsDivider
import kotlinx.android.synthetic.main.product_layout.characteristicsRecycler
import kotlinx.android.synthetic.main.product_layout.description
import kotlinx.android.synthetic.main.product_layout.descriptionDivider
import kotlinx.android.synthetic.main.product_layout.descriptionEndDivider
import kotlinx.android.synthetic.main.product_layout.descriptionTitle
import kotlinx.android.synthetic.main.product_layout.gradientDescription
import kotlinx.android.synthetic.main.product_layout.nextCharacteristics
import kotlinx.android.synthetic.main.product_layout.nextDescription
import kotlinx.android.synthetic.main.product_layout.photosSize
import kotlinx.android.synthetic.main.product_layout.picturePager
import kotlinx.android.synthetic.main.product_layout.price
import kotlinx.android.synthetic.main.product_layout.productTitle
import kotlinx.android.synthetic.main.product_layout.progress
import kotlinx.android.synthetic.main.product_layout.statusProduct
import kotlinx.android.synthetic.main.search_layout.defaultError
import kotlinx.android.synthetic.main.search_layout.offlineState
import kotlinx.android.synthetic.main.search_layout.searchNotFound
import kotlinx.android.synthetic.main.search_not_found_state.notFoundDescription
import java.net.UnknownHostException

interface ProductUi : BaseUi {
    fun openMoreCharacteristics(): Observable<Unit>
    fun openFullDescription(): Observable<Unit>
    fun retryButton(): Observable<Unit>
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

    override fun openMoreCharacteristics(): Observable<Unit> {
        return characteristicsRecycler.clicks()
    }

    override fun openFullDescription(): Observable<Unit> {
        return description.clicks()
    }

    override fun retryButton(): Observable<Unit> {
        return retryButton.clicks()
    }

    override fun render(state: ProductViewState) {
        hideAllErrorsState()

        if (state.isLoading) {
            showProgress()
            return
        }

        if (state.isShowFullDescription || state.isShowFullCharacteristics) {
            Intent(this, ProductExtraDetailActivity::class.java).also {
                startActivity(it)
            }

            return
        }

        if (state.stateError != null) {
            when (state.stateError) {
                is NoResultFoundException -> {
                    showSearchError(state.stateError.queryString)
                }
                is UnknownHostException -> showOfflineState()
                else -> showDefaultError()
            }
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

        state.productDetail?.description?.takeIf { state.isDescriptionPresentation }?.let {
            hideProgress()
            showProductDescription(it)
        }
    }

    private fun hideAllErrorsState() {
        searchNotFound.isVisible = false
        defaultError.isVisible = false
        offlineState.isVisible = false
    }

    private fun showDefaultError() {
        defaultErrorDescription.setText(R.string.malformed_product_id)
        defaultError.isVisible = true
        hideProgress()
    }

    private fun showOfflineState() {
        offlineState.isVisible = true
        hideProgress()
    }

    private fun showSearchError(queryString: String) {
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

    private fun showPhotoGallery(productDetail: ProductDetail) {
        if (productDetail.pictures.isNotEmpty()) {
            val format = getString(R.string.num_photos)
            photosSize.text = format.format(productDetail.pictures.size)
            photosSize.isVisible = true

            val productImageAdapter = ProductImageAdapter(this, productDetail.pictures)
            picturePager.adapter = productImageAdapter
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

        nextCharacteristics.isVisible = true
        characteristicsDivider.isVisible = true
        characteristics.isVisible = true
        characteristicsRecycler.isVisible = true
    }

    private fun showProductDescription(productDescription: ProductDescription) {
        if (productDescription.text.isNotEmpty()) {
            description.text = productDescription.text
        } else {
            description.text = productDescription.plainText
        }

        nextDescription.isVisible = true
        descriptionDivider.isVisible = true
        descriptionTitle.isVisible = true
        description.isVisible = true
        gradientDescription.isVisible = true
        descriptionEndDivider.isVisible = true
    }

    private fun showProgress() {
        progress.isVisible = true
    }

    private fun hideProgress() {
        progress.isVisible = false
    }
}
