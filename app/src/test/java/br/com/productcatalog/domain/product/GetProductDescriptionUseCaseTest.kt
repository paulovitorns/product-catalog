package br.com.productcatalog.domain.product

import br.com.productcatalog.data.models.ProductDescription
import br.com.productcatalog.library.reactivex.TestSchedulerProvider
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Observable
import org.junit.Test

class GetProductDescriptionUseCaseTest {

    private val repository = mock<ProductDetailRepository>()
    private val schedulerProvider = TestSchedulerProvider()
    private val getProductDescriptionUseCase = GetProductDescriptionUseCase(
        repository,
        schedulerProvider
    )

    @Test
    fun `success on get product description`() {
        val productId = "123456"
        val expected = ProductDescription(
            text = "text",
            plainText = "plain text"
        )

        given { repository.fetchProductDescription(productId) }.willReturn(Observable.just(expected))

        getProductDescriptionUseCase(productId)
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue(expected)
            .awaitTerminalEvent()

        verify(repository).fetchProductDescription(productId)
    }
}
