package br.com.productcatalog.data.product

import br.com.productcatalog.data.models.ProductDescription
import br.com.productcatalog.data.models.ProductDetail
import br.com.productcatalog.data.search.ResultNotFoundException
import br.com.productcatalog.library.retrofit.endpoint.ProductDetailEndpoint
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Observable
import org.junit.Test
import retrofit2.Response

class ProductDetailRepositoryTest {

    private val endpoint = mock<ProductDetailEndpoint>()
    private val productDetailRepository = DefaultProductDetailRepository(endpoint)

    @Test
    fun `get details from a product by its ID`() {
        val productId = "12345"
        val expected = ProductDetail()

        given { endpoint.fetchProductDetail(productId) }.willReturn(Observable.just(Response.success(expected)))

        productDetailRepository.fetchProductDetail(productId)
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue(expected)
            .awaitTerminalEvent()

        verify(endpoint).fetchProductDetail(productId)
    }

    @Test
    fun `thrown ResultNotFoundException when fail to fetch a product by its ID`() {
        val productId = "12345"
        val expected: ProductDetail? = null

        given {
            endpoint.fetchProductDetail(productId)
        }.willReturn(Observable.just(Response.success(expected)))

        productDetailRepository.fetchProductDetail(productId)
            .test()
            .assertError(ResultNotFoundException::class.java)
            .assertNotComplete()
            .assertNoValues()
            .awaitTerminalEvent()

        verify(endpoint).fetchProductDetail(productId)
    }

    @Test
    fun `success on get product description`() {
        val productId = "123456"
        val expected = ProductDescription()

        given { endpoint.fetchProductDescription(productId) }.willReturn(Observable.just(Response.success(expected)))

        productDetailRepository.fetchProductDescription(productId)
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue(expected)
            .awaitTerminalEvent()

        verify(endpoint).fetchProductDescription(productId)
    }

    @Test
    fun `thrown DescriptionNotFoundException when fail to fetch the product description by its ID`() {
        val productId = "123456"
        val expected: ProductDescription? = null

        given { endpoint.fetchProductDescription(productId) }.willReturn(Observable.just(Response.success(expected)))

        productDetailRepository.fetchProductDescription(productId)
            .test()
            .assertError(DescriptionNotFoundException::class.java)
            .assertNotComplete()
            .assertNoValues()
            .awaitTerminalEvent()

        verify(endpoint).fetchProductDescription(productId)
    }
}
