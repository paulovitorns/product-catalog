package br.com.productcatalog.data.search

import br.com.productcatalog.data.models.ProductResult
import br.com.productcatalog.data.models.SearchPaging
import br.com.productcatalog.data.models.SearchResult
import br.com.productcatalog.library.retrofit.endpoint.SearchEndpoint
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Observable
import org.junit.Test
import retrofit2.Response

class SearchRepositoryTest {

    private val endpoint = mock<SearchEndpoint>()
    private val searchRepository = DefaultSearchRepository(endpoint)

    @Test
    fun `get success on searching a random products in MLB site`() {
        val siteId = "MLB"
        val query = "mock"
        val limit = 10

        val expected = SearchResult(
            paging = SearchPaging(10, 0, 10, 10)
        )

        given {
            endpoint.searchFromSite(siteId, query, limit)
        }.willReturn(Observable.just(Response.success(expected)))

        searchRepository.searchProduct(siteId, query, limit)
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue(expected)
            .awaitTerminalEvent()

        verify(endpoint).searchFromSite(siteId, query, limit)
    }

    @Test
    fun `throw NoResultFoundException on searching a random products in MLB site`() {
        val siteId = "MLB"
        val query = "mock"
        val limit = 10

        val expected = SearchResult()

        given {
            endpoint.searchFromSite(siteId, query, limit)
        }.willReturn(Observable.just(Response.success(expected)))

        searchRepository.searchProduct(siteId, query, limit)
            .test()
            .assertError(ResultNotFoundException::class.java)
            .assertNotComplete()
            .assertNoValues()
            .awaitTerminalEvent()

        verify(endpoint).searchFromSite(siteId, query, limit)
    }

    @Test
    fun `given the limit is 10 products per page and there's 20 products gets success on fetch more products`() {
        val firstResult = SearchResult(
            siteId = "MLB",
            query = "mock",
            paging = SearchPaging(total = 21, offset = 0, limit = 10)
        )

        val expected = SearchResult(
            siteId = "MLB",
            query = "mock",
            paging = SearchPaging(total = 21, offset = 10, limit = 10),
            results = mutableListOf(ProductResult(), ProductResult())
        )

        given {
            endpoint.paginateSearchResult(firstResult.siteId, firstResult.query, limit = 10, offset = 10)
        }.willReturn(Observable.just(Response.success(expected)))

        searchRepository.paginateResult(firstResult.siteId, firstResult.query, limit = 10, offset = 10)
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue(expected)
            .awaitTerminalEvent()

        verify(endpoint).paginateSearchResult(firstResult.siteId, firstResult.query, limit = 10, offset = 10)
    }

    @Test
    fun `throw AllItemsLoadedException after load all products`() {
        val lastResult = SearchResult(
            siteId = "MLB",
            query = "mock",
            paging = SearchPaging(total = 21, offset = 20, limit = 10)
        )

        val expected = SearchResult(
            siteId = "MLB",
            query = "mock",
            paging = SearchPaging(total = 21, offset = 30, limit = 10),
            results = mutableListOf()
        )

        given {
            endpoint.paginateSearchResult(lastResult.siteId, lastResult.query, limit = 10, offset = 30)
        }.willReturn(Observable.just(Response.success(expected)))

        searchRepository.paginateResult(lastResult.siteId, lastResult.query, limit = 10, offset = 30)
            .test()
            .assertError(AllItemsLoadedException::class.java)
            .assertNotComplete()
            .assertNoValues()
            .awaitTerminalEvent()

        verify(endpoint).paginateSearchResult(lastResult.siteId, lastResult.query, limit = 10, offset = 30)
    }
}
