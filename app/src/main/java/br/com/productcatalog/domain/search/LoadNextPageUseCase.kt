package br.com.productcatalog.domain.search

import br.com.productcatalog.data.models.SearchResult
import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.library.reactivex.SchedulerProvider
import br.com.productcatalog.library.reactivex.applyObservableSchedulers
import io.reactivex.Observable
import javax.inject.Inject

@ActivityScope
class LoadNextPageUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
    private val schedulerProvider: SchedulerProvider
) {

    operator fun invoke(lastSearchResult: SearchResult): Observable<SearchResult> {
        return searchRepository.paginateResult(
            site = lastSearchResult.siteId,
            queryString = lastSearchResult.query,
            limit = lastSearchResult.paging.limit,
            offset = lastSearchResult.paging.offset + lastSearchResult.paging.limit
        ).compose(applyObservableSchedulers(schedulerProvider))
    }
}
