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

    private var pagesSize = -1
    private var searchRef = ""

    operator fun invoke(lastSearchResult: SearchResult): Observable<SearchResult> {

        // check if the search has changed and reset to the default values
        if (searchRef.isEmpty() || lastSearchResult.query != searchRef) {
            searchRef = lastSearchResult.query
            pagesSize = -1
        }

        val currentOffset = lastSearchResult.paging.offset
        // retrieve total of items of the last search
        val resultSize = lastSearchResult.paging.total
        // increment next offset
        val nextOffset = currentOffset + lastSearchResult.paging.limit

        if (pagesSize == -1) {
            // calculate the pages based on the size of the items minus the first page loaded
            pagesSize = Math.round((resultSize / lastSearchResult.paging.limit).toDouble()).toInt()
        }

        // verify if remains some item to fetch
        if (pagesSize <= 0) {
            // set to default value
            pagesSize = -1
            // if not has more pages throw an exception
            return Observable.error(AllItemsLoadedException())
        }

        // decrease the next search limit from
        pagesSize -= 1

        return searchRepository.paginateResult(
            site = lastSearchResult.siteId,
            queryString = lastSearchResult.query,
            limit = lastSearchResult.paging.limit,
            offset = nextOffset
        ).compose(applyObservableSchedulers(schedulerProvider))
    }
}
