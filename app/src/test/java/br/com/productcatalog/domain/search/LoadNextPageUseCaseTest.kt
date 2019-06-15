package br.com.productcatalog.domain.search

import br.com.productcatalog.data.models.SearchPaging
import br.com.productcatalog.data.models.SearchResult
import br.com.productcatalog.library.reactivex.TestSchedulerProvider
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Observable
import org.junit.Test

class LoadNextPageUseCaseTest {

    private val repository = mock<SearchRepository>()
    private val schedulerProvider = TestSchedulerProvider()

    private val nextPageUseCase = LoadNextPageUseCase(
        repository,
        schedulerProvider
    )

    @Test
    fun `get success on fetch the next products page`() {
        val firstResult = SearchResult(
            siteId = "MLB",
            query = "mock",
            paging = SearchPaging(total = 21, offset = 0, limit = 10)
        )

        val expected = SearchResult(
            siteId = "MLB",
            query = "mock",
            paging = SearchPaging(total = 21, offset = 10, limit = 10)
        )

        given {
            repository.paginateResult(firstResult.siteId, firstResult.query, limit = 10, offset = 10)
        }.willReturn(Observable.just(expected))

        nextPageUseCase(firstResult)
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue(expected)
            .awaitTerminalEvent()

        verify(repository).paginateResult(firstResult.siteId, firstResult.query, limit = 10, offset = 10)
    }
}