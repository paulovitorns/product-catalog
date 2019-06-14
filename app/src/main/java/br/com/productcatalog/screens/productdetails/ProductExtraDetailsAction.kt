package br.com.productcatalog.screens.productdetails

import br.com.productcatalog.data.models.ProductDetail

sealed class ProductExtraDetailsAction {
    data class OpenCharacteristics(val productDetail: ProductDetail) : ProductExtraDetailsAction()
    data class OpenDescription(val productDetail: ProductDetail) : ProductExtraDetailsAction()
}
