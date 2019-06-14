package br.com.productcatalog.domain.product

import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.screens.product.ProductPartialState
import br.com.productcatalog.screens.product.ProductPartialState.Loading
import br.com.productcatalog.screens.product.ProductViewAction
import br.com.productcatalog.screens.product.ProductViewAction.LoadProductDescription
import br.com.productcatalog.screens.product.ProductViewAction.LoadProductDetail
import br.com.productcatalog.screens.product.ProductViewAction.RestoreLastState
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import javax.inject.Inject

@ActivityScope
class ProductDetailComposer @Inject constructor(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val getProductDescriptionUseCase: GetProductDescriptionUseCase,
    private val productMapper: ProductResultMapper
) {

    fun bindActions(): ObservableTransformer<ProductViewAction, ProductPartialState> {
        return ObservableTransformer { action ->
            action.publish { shared ->
                Observable.mergeArray(
                    shared.ofType(LoadProductDetail::class.java).compose(fetchProductDetailAction()),
                    shared.ofType(LoadProductDescription::class.java).compose(fetchProductDescriptionAction()),
                    shared.ofType(RestoreLastState::class.java).compose(restoreLastStateAction())
                )
            }
        }
    }

    private fun fetchProductDetailAction(): ObservableTransformer<LoadProductDetail, ProductPartialState> {
        return ObservableTransformer { observer ->
            observer.flatMap { action ->
                getProductDetailUseCase(action.productId)
                    .map { productMapper.stateOf(action, it) }
                    .onErrorReturn { productMapper.errorOf(action, it) }
                    .startWith(Loading)
            }
        }
    }

    private fun fetchProductDescriptionAction(): ObservableTransformer<LoadProductDescription, ProductPartialState> {
        return ObservableTransformer { observer ->
            observer.flatMap { action ->
                getProductDescriptionUseCase(action.productId)
                    .map { productMapper.stateOf(action, it) }
                    .onErrorReturn { productMapper.errorOf(action, it) }
                    .startWith(Loading)
            }
        }
    }

    private fun restoreLastStateAction(): ObservableTransformer<RestoreLastState, ProductPartialState> {
        return ObservableTransformer { observer ->
            observer.flatMap { action ->
                Observable.just(action)
                    .map { productMapper.stateOf(action, it) }
                    .onErrorReturn { productMapper.errorOf(action, it) }
                    .startWith(Loading)
            }
        }
    }
}
