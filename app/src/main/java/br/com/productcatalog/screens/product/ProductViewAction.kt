package br.com.productcatalog.screens.product

sealed class ProductViewAction {
    data class LoadProductDetail(val productId: String) : ProductViewAction()
    data class LoadProductDescription(val productId: String) : ProductViewAction()
    data class RestoreLastState(val lastViewState: ProductViewState) : ProductViewAction()
}
