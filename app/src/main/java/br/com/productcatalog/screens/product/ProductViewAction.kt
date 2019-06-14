package br.com.productcatalog.screens.product

import br.com.productcatalog.data.models.ProductDetail
import br.com.productcatalog.data.models.ProductResult

sealed class ProductViewAction {
    data class LoadProductDetail(val productResult: ProductResult) : ProductViewAction()
    data class LoadProductDescription(val productId: String) : ProductViewAction()
    data class RestoreLastState(val lastViewState: ProductViewState) : ProductViewAction()
    data class OpenFullCharacteristics(val productDetail: ProductDetail) : ProductViewAction()
    data class OpenFullDescription(val productDescription: ProductDetail) : ProductViewAction()
}
