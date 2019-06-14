package br.com.productcatalog.domain.product

import br.com.productcatalog.data.models.ProductDescription
import br.com.productcatalog.data.models.ProductDetail
import io.reactivex.Observable

interface ProductDetailRepository {
    fun fetchProductDetail(productId: String): Observable<ProductDetail>
    fun fetchProductDescription(productId: String): Observable<ProductDescription>
}
