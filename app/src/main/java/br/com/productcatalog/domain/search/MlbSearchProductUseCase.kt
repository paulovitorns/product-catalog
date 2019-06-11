package br.com.productcatalog.domain.search

import br.com.productcatalog.data.models.SearchResult
import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.library.reactivex.SchedulerProvider
import br.com.productcatalog.library.reactivex.applyObservableSchedulers
import io.reactivex.Observable
import javax.inject.Inject

@ActivityScope
class MlbSearchProductUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
    private val schedulerProvider: SchedulerProvider
) {
    operator fun invoke(queryString: String): Observable<SearchResult> {
        return searchRepository.searchProduct(
            site = "MLB",
            queryString = queryString,
            limit = 10
        ).compose(applyObservableSchedulers(schedulerProvider))
    }
}
