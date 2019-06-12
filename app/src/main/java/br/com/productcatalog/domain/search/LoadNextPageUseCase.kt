package br.com.productcatalog.domain.search

import br.com.productcatalog.data.models.SearchResult
import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.library.reactivex.SchedulerProvider
import br.com.productcatalog.library.reactivex.applyObservableSchedulers
import io.reactivex.Observable
import javax.inject.Inject

class AllItemsLoadedException : IllegalArgumentException()

@ActivityScope
class LoadNextPageUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
    private val schedulerProvider: SchedulerProvider
) {

    private var remainingItems = -1

    operator fun invoke(lastSearchResult: SearchResult): Observable<SearchResult> {
        val currentOffset = lastSearchResult.paging.offset

        // retrieve total of items of the last search
        val resultSize = lastSearchResult.paging.total

        // increment next offset
        val nextOffset = currentOffset + lastSearchResult.paging.limit

        if (remainingItems == -1) {
            // retrieve the searchResult size minus the first page loaded
            remainingItems = resultSize - lastSearchResult.paging.limit
        }

        // verify if remains some item to fetch
        if (remainingItems <= 0) {
            // if not has more pages throw an exception
            return Observable.error(AllItemsLoadedException())
        }

        // decrease the next search limit from
        remainingItems -= lastSearchResult.paging.limit

        return searchRepository.paginateResult(
            site = lastSearchResult.siteId,
            queryString = lastSearchResult.query,
            limit = lastSearchResult.paging.limit,
            offset = nextOffset
        ).compose(applyObservableSchedulers(schedulerProvider))
    }
}
