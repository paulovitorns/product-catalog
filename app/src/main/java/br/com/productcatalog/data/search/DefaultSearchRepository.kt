package br.com.productcatalog.data.search

import br.com.productcatalog.data.models.SearchResult
import br.com.productcatalog.domain.search.SearchRepository
import br.com.productcatalog.library.retrofit.endpoint.SearchEndpoint
import io.reactivex.Observable
import javax.inject.Inject

class DefaultSearchRepository @Inject constructor(
    private val searchEndpoint: SearchEndpoint
) : SearchRepository {

    override fun searchProduct(site: String, queryString: String, limit: Int): Observable<SearchResult> {
        return searchEndpoint.searchFromSite(site, queryString, limit).map { response ->
            when (response.code()) {
                200 -> response.body()
                else -> throw IllegalArgumentException(response.errorBody().toString())
            }
        }
    }

    override fun paginateResult(
        site: String,
        queryString: String,
        limit: Int,
        offset: Int
    ): Observable<SearchResult> {
        return searchEndpoint.paginateSearchResult(site, queryString, limit, offset).map { response ->
            when (response.code()) {
                200 -> response.body()
                else -> throw IllegalArgumentException(response.errorBody().toString())
            }
        }
    }
}
