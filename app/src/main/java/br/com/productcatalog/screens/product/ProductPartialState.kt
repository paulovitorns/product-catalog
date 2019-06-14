package br.com.productcatalog.screens.product

import br.com.productcatalog.data.models.ProductDescription
import br.com.productcatalog.data.models.ProductDetail
import br.com.productcatalog.data.models.ProductResult

sealed class ProductPartialState {
    object Loading : ProductPartialState()
    data class ProductDetailLoaded(val productDetail: ProductDetail) : ProductPartialState()
    data class ProductDescriptionLoaded(val productDescription: ProductDescription) : ProductPartialState()
    data class LastViewStateRestored(val lastViewState: ProductViewState) : ProductPartialState()
    data class StateError(val error: Throwable, val productToOpen: ProductResult? = null) : ProductPartialState()
    data class FullCharacteristicsOpened(val productDetail: ProductDetail) : ProductPartialState()
    data class FullDescriptionOpened(val productDescription: ProductDetail) : ProductPartialState()
}
