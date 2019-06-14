package br.com.productcatalog.data.product

import br.com.productcatalog.data.models.ProductDescription
import br.com.productcatalog.data.models.ProductDetail
import br.com.productcatalog.data.search.NoResultFoundException
import br.com.productcatalog.domain.product.ProductDetailRepository
import br.com.productcatalog.library.retrofit.endpoint.ProductDetailEndpoint
import io.reactivex.Observable
import javax.inject.Inject

class DefaultProductDetailRepository @Inject constructor(
    private val productDetailEndpoint: ProductDetailEndpoint
) : ProductDetailRepository {

    override fun fetchProductDetail(productId: String): Observable<ProductDetail> {
        return productDetailEndpoint.fetchProductDetail(productId).map { response ->
            when (response.code()) {
                200 -> {
                    val responseBody = response.body()
                    responseBody ?: throw NoResultFoundException(productId)
                }
                else -> throw IllegalArgumentException(response.errorBody().toString())
            }
        }
    }

    override fun fetchProductDescription(productId: String): Observable<ProductDescription> {
        return productDetailEndpoint.fetchProductDescription(productId).map { response ->
            when (response.code()) {
                200 -> {
                    val responseBody = response.body()
                    responseBody ?: throw NoResultFoundException(productId)
                }
                else -> throw IllegalArgumentException(response.errorBody().toString())
            }
        }
    }
}
