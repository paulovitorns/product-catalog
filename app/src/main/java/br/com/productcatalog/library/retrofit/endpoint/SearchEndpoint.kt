package br.com.productcatalog.library.retrofit.endpoint

import br.com.productcatalog.data.models.SearchResult
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SearchEndpoint {

    @GET("sites/{site}/search")
    fun searchFromSite(
        @Path("site") site: String,
        @Query("q") queryString: String,
        @Query("limit") limit: Int
    ): Observable<Response<SearchResult>>

    @GET("sites/{site}/search")
    fun paginateSearchResult(
        @Path("site") site: String,
        @Query("q") queryString: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Observable<Response<SearchResult>>
}
