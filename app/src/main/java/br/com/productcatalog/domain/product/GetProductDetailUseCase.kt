package br.com.productcatalog.domain.product

import br.com.productcatalog.data.models.ProductDetail
import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.library.reactivex.SchedulerProvider
import br.com.productcatalog.library.reactivex.applyObservableSchedulers
import io.reactivex.Observable
import javax.inject.Inject

@ActivityScope
class GetProductDetailUseCase @Inject constructor(
    private val productReRepository: ProductDetailRepository,
    private val schedulerProvider: SchedulerProvider
) {
    operator fun invoke(productId: String): Observable<ProductDetail> {
        return productReRepository.fetchProductDetail(productId)
            .compose(applyObservableSchedulers(schedulerProvider))
    }
}
