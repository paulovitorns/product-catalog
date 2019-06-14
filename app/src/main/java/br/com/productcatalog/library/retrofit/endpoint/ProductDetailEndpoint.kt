package br.com.productcatalog.library.retrofit.endpoint

import br.com.productcatalog.data.models.ProductDescription
import br.com.productcatalog.data.models.ProductDetail
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductDetailEndpoint {

    @GET("items/{productId}")
    fun fetchProductDetail(
        @Path("productId") productId: String
    ): Observable<Response<ProductDetail>>

    @GET("items/{productId}/description")
    fun fetchProductDescription(
        @Path("productId") productId: String
    ): Observable<Response<ProductDescription>>
}
