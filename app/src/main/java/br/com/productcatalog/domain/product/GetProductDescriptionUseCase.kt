package br.com.productcatalog.domain.product

import br.com.productcatalog.data.models.ProductDescription
import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.library.reactivex.SchedulerProvider
import br.com.productcatalog.library.reactivex.applyObservableSchedulers
import io.reactivex.Observable
import javax.inject.Inject

@ActivityScope
class GetProductDescriptionUseCase @Inject constructor(
    private val productReRepository: ProductDetailRepository,
    private val schedulerProvider: SchedulerProvider
) {
    operator fun invoke(productId: String): Observable<ProductDescription> {
        return productReRepository.fetchProductDescription(productId)
            .compose(applyObservableSchedulers(schedulerProvider))
    }
}
