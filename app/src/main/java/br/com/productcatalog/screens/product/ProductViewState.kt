package br.com.productcatalog.screens.product

import br.com.productcatalog.data.models.ProductDetail
import br.com.productcatalog.data.models.ProductResult

data class ProductViewState(
    // stored the product Id to be used in case of lost connection os something like that
    val productToOpen: ProductResult? = null,
    // indicates that's there's something working
    val isLoading: Boolean = false,
    // indicates that's there's a fresh product to show
    val isProductPresentation: Boolean = false,
    // indicates that's there's a new description to show
    val isDescriptionPresentation: Boolean = false,
    val isShowFullCharacteristics: Boolean = false,
    val isShowFullDescription: Boolean = false,
    // used to store data result from fetch a product detail
    val productDetail: ProductDetail? = null,
    // indicates that's occurs some error while loading the product detail
    val stateError: Throwable? = null
) {

    fun builder(): Builder {
        return Builder(this)
    }

    class Builder(searchViewState: ProductViewState) {
        private var productToOpen: ProductResult? = searchViewState.productToOpen
        private var isLoading = searchViewState.isLoading
        private var isProductPresentation = searchViewState.isProductPresentation
        private var isDescriptionPresentation = searchViewState.isDescriptionPresentation
        private var isShowFullCharacteristics = searchViewState.isShowFullCharacteristics
        private var isShowFullDescription = searchViewState.isShowFullDescription
        private var productDetail: ProductDetail? = searchViewState.productDetail
        private var stateError: Throwable? = searchViewState.stateError

        fun setProductToOpen(productResult: ProductResult?): Builder {
            this.productToOpen = productResult
            return this
        }

        fun setLoading(flag: Boolean): Builder {
            isLoading = flag
            return this
        }

        fun setProductPresentation(flag: Boolean): Builder {
            this.isProductPresentation = flag
            return this
        }

        fun setDescriptionPresentation(flag: Boolean): Builder {
            this.isDescriptionPresentation = flag
            return this
        }

        fun setShowFullCharacteristics(flag: Boolean): Builder {
            this.isShowFullCharacteristics = flag
            return this
        }

        fun setShowFullDescription(flag: Boolean): Builder {
            this.isShowFullDescription = flag
            return this
        }

        fun setProductDetail(productDetail: ProductDetail?): Builder {
            this.productDetail = productDetail
            return this
        }

        fun setStateError(error: Throwable?): Builder {
            this.stateError = error
            return this
        }

        fun build(): ProductViewState {
            return ProductViewState(
                productToOpen,
                isLoading,
                isProductPresentation,
                isDescriptionPresentation,
                isShowFullCharacteristics,
                isShowFullDescription,
                productDetail,
                stateError
            )
        }
    }
}
