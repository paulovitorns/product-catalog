package br.com.productcatalog.screens.product

import android.content.Intent
import android.graphics.PorterDuff
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
import br.com.productcatalog.library.extension.color
import br.com.productcatalog.library.extension.toMoney
import br.com.productcatalog.library.extension.topAlignDecimalChars
import br.com.productcatalog.screens.BaseActivity
import br.com.productcatalog.screens.BaseUi
import br.com.productcatalog.screens.productdetails.ProductExtraDetailActivity
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.default_error_state.defaultErrorDescription
import kotlinx.android.synthetic.main.offline_state.retryButton
import kotlinx.android.synthetic.main.product_layout.appToolbar
import kotlinx.android.synthetic.main.product_layout.characteristicsDivider
import kotlinx.android.synthetic.main.product_layout.characteristicsRecycler
import kotlinx.android.synthetic.main.product_layout.characteristicsTitle
import kotlinx.android.synthetic.main.product_layout.description
import kotlinx.android.synthetic.main.product_layout.descriptionDivider
import kotlinx.android.synthetic.main.product_layout.descriptionEndDivider
import kotlinx.android.synthetic.main.product_layout.descriptionTitle
import kotlinx.android.synthetic.main.product_layout.gradientDescription
import kotlinx.android.synthetic.main.product_layout.nextCharacteristics
import kotlinx.android.synthetic.main.product_layout.nextDescription
import kotlinx.android.synthetic.main.product_layout.payment
import kotlinx.android.synthetic.main.product_layout.paymentIcon
import kotlinx.android.synthetic.main.product_layout.photosSize
import kotlinx.android.synthetic.main.product_layout.picturePager
import kotlinx.android.synthetic.main.product_layout.priceProduct
import kotlinx.android.synthetic.main.product_layout.productTitle
import kotlinx.android.synthetic.main.product_layout.progress
import kotlinx.android.synthetic.main.product_layout.rateFree
import kotlinx.android.synthetic.main.product_layout.shipment
import kotlinx.android.synthetic.main.product_layout.shipmentIcon
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
    private val characteristicAdapter = CharacteristicAdapter()

    override fun setupToolbar() {
        super.setupToolbar()
        setSupportActionBar(appToolbar)
        title = ""
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun setupViews() {
        super.setupViews()
        characteristicsRecycler.adapter = characteristicAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun openMoreCharacteristics(): Observable<Unit> {
        return characteristicAdapter.onItemSelected().map {
            // we don't need the item here
            // skipping this map
        }
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
            hideProgress()
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
            hideProgress()
            return
        }

        state.productDetail?.takeIf { state.isProductPresentation }?.let {
            with(state.productDetail) {
                showPhotoGallery()
                showProductDetails()
                showProductInstallments()
                showProductShipment()
                showCharacteristics()
            }
            hideProgress()
        }

        state.productDetail?.description?.takeIf { state.isDescriptionPresentation }?.let {
            showProductDescription(it)
            hideProgress()
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
    }

    private fun showOfflineState() {
        offlineState.isVisible = true
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
    }

    private fun ProductDetail.showPhotoGallery() {
        if (this.pictures.isNotEmpty()) {
            val format = getString(R.string.num_photos)
            photosSize.text = format.format(this.pictures.size)
            photosSize.isVisible = true

            val productImageAdapter = ProductImageAdapter(this@ProductActivity, this.pictures)
            picturePager.adapter = productImageAdapter
        }
    }

    private fun ProductDetail.showProductDetails() {
        productTitle.text = this.title
        priceProduct.text = this.price.toMoney(this.currencyId).topAlignDecimalChars()

        val formatStatus = getString(R.string.products_status)
        val productCondition = if (this.condition.toLowerCase() == "new") {
            getString(R.string.new_product)
        } else {
            getString(R.string.used_product)
        }
        statusProduct.text = formatStatus.format(productCondition, this.soldQuantity)
    }

    private fun ProductDetail.showCharacteristics() {

        fun List<Characteristic>.getTopFourItems(): List<Characteristic> {
            return this.take(4).reversed()
        }

        if (this.characteristics?.isEmpty() == true) {
            return
        }

        characteristicAdapter.addItems(this.characteristics!!.getTopFourItems())

        nextCharacteristics.isVisible = true
        characteristicsDivider.isVisible = true
        characteristicsTitle.isVisible = true
        characteristicsRecycler.isVisible = true
    }

    private fun ProductDetail.showProductInstallments() {

        if (this.installments == null) return

        val installments = "%s %s"

        payment.text = installments.format(
            "${this.installments?.quantity}x",
            this.installments?.amount?.toMoney(this.installments?.currency ?: "")
        ).topAlignDecimalChars()

        if (this.installments!!.rate <= 0) {
            payment.setTextColor(color(R.color.green))
            paymentIcon.setColorFilter(color(R.color.green), PorterDuff.Mode.SRC_IN)
            rateFree.isVisible = true
        }

        payment.isVisible = true
        paymentIcon.isVisible = true
    }

    private fun ProductDetail.showProductShipment() {
        if (this.shipping == null) return

        if (this.shipping.freeShipping) {
            shipment.text = getString(R.string.free_shipping)
            shipment.setTextColor(color(R.color.green))
            shipmentIcon.setColorFilter(color(R.color.green), PorterDuff.Mode.SRC_IN)
            shipmentIcon.isVisible = true
            shipment.isVisible = true
        }
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
