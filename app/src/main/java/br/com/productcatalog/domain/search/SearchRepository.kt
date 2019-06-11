package br.com.productcatalog.domain.search

import br.com.productcatalog.data.models.SearchResult
import io.reactivex.Observable

interface SearchRepository {
    fun searchProduct(site: String, queryString: String, limit: Int): Observable<SearchResult>
    fun paginateResult(site: String, queryString: String, limit: Int, offset: Int): Observable<SearchResult>
}
