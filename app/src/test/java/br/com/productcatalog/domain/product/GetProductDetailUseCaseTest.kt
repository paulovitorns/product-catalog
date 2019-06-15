package br.com.productcatalog.domain.product

import br.com.productcatalog.data.models.ProductDetail
import br.com.productcatalog.library.reactivex.TestSchedulerProvider
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Observable
import org.junit.Test

class GetProductDetailUseCaseTest {

    private val repository = mock<ProductDetailRepository>()
    private val schedulerProvider = TestSchedulerProvider()
    private val getProductDetailUseCase = GetProductDetailUseCase(
        repository,
        schedulerProvider
    )

    @Test
    fun `get details from a product passing its ID`() {
        val productId = "12345"
        val expected = ProductDetail()

        given { repository.fetchProductDetail(productId) }.willReturn(Observable.just(expected))

        getProductDetailUseCase(productId)
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue(expected)
            .awaitTerminalEvent()

        verify(repository).fetchProductDetail(productId)
    }
}
