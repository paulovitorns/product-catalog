package br.com.productcatalog.domain.search

import br.com.productcatalog.data.models.SearchResult
import br.com.productcatalog.library.reactivex.TestSchedulerProvider
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Observable
import org.junit.Test

class MlbSearchProductUseCaseTest {

    private val repository = mock<SearchRepository>()
    private val schedulerProvider = TestSchedulerProvider()
    private val mlbSearchProductUseCase = MlbSearchProductUseCase(
        repository,
        schedulerProvider
    )

    @Test
    fun `get success on search a random products in MLB site`() {
        val siteId = "MLB"
        val query = "mock"
        val limit = 10

        val expected = SearchResult()

        given {
            repository.searchProduct(siteId, query, limit)
        }.willReturn(Observable.just(expected))

        mlbSearchProductUseCase(query)
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue(expected)
            .awaitTerminalEvent()

        verify(repository).searchProduct(siteId, query, limit)
    }
}
