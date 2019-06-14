package br.com.productcatalog.screens.product

import br.com.productcatalog.data.models.ProductDetail

sealed class ProductViewAction {
    data class LoadProductDetail(val productId: String) : ProductViewAction()
    data class LoadProductDescription(val productId: String) : ProductViewAction()
    data class RestoreLastState(val lastViewState: ProductViewState) : ProductViewAction()
    data class OpenFullCharacteristics(val productDetail: ProductDetail) : ProductViewAction()
    data class OpenFullDescription(val productDescription: ProductDetail) : ProductViewAction()
}
