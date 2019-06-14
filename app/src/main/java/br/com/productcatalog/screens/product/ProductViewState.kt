package br.com.productcatalog.screens.product

import br.com.productcatalog.data.models.ProductDescription
import br.com.productcatalog.data.models.ProductDetail

data class ProductViewState(
    // stored the product Id to be used in case of lost connection os something like that
    val productId: String? = null,
    // indicates that's there's something working
    val isLoading: Boolean = false,
    // indicates that's there's a fresh product to show
    val isProductPresentation: Boolean = false,
    // indicates that's there's a new description to show
    val isDescriptionPresentation: Boolean = false,
    // used to store data result from fetch a product detail
    val productDetail: ProductDetail? = null,
    // used to store data result from fetch a product description
    val productDescription: ProductDescription? = null,
    // indicates that's occurs some error while loading the product detail
    val stateError: Throwable? = null
) {

    fun builder(): Builder {
        return Builder(this)
    }

    class Builder(searchViewState: ProductViewState) {
        private var productId: String? = searchViewState.productId
        private var isLoading = searchViewState.isLoading
        private var isProductPresentation = searchViewState.isProductPresentation
        private var isDescriptionPresentation = searchViewState.isDescriptionPresentation
        private var productDetail: ProductDetail? = searchViewState.productDetail
        private var productDescription: ProductDescription? = searchViewState.productDescription
        private var stateError: Throwable? = searchViewState.stateError

        fun setProductId(productId: String?): Builder {
            this.productId = productId
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

        fun setProductDetail(productDetail: ProductDetail??): Builder {
            this.productDetail = productDetail
            return this
        }

        fun setProductDescription(productDescription: ProductDescription?): Builder {
            this.productDescription = productDescription
            return this
        }

        fun setStateError(error: Throwable?): Builder {
            this.stateError = error
            return this
        }

        fun build(): ProductViewState {
            return ProductViewState(
                productId,
                isLoading,
                isProductPresentation,
                isDescriptionPresentation,
                productDetail,
                productDescription,
                stateError
            )
        }
    }
}
