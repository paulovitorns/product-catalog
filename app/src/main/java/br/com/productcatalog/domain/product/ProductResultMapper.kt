package br.com.productcatalog.domain.product

import br.com.productcatalog.data.models.ProductDescription
import br.com.productcatalog.data.models.ProductDetail
import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.screens.product.ProductPartialState
import br.com.productcatalog.screens.product.ProductPartialState.LastViewStateRestored
import br.com.productcatalog.screens.product.ProductPartialState.ProductDescriptionLoaded
import br.com.productcatalog.screens.product.ProductPartialState.ProductDetailLoaded
import br.com.productcatalog.screens.product.ProductPartialState.StateError
import br.com.productcatalog.screens.product.ProductViewAction
import br.com.productcatalog.screens.product.ProductViewAction.LoadProductDescription
import br.com.productcatalog.screens.product.ProductViewAction.LoadProductDetail
import br.com.productcatalog.screens.product.ProductViewAction.RestoreLastState
import javax.inject.Inject

@ActivityScope
class ProductResultMapper @Inject constructor() {

    fun <T> stateOf(action: ProductViewAction, result: T): ProductPartialState {
        return when (action) {
            is LoadProductDetail -> ProductDetailLoaded(result as ProductDetail)
            is LoadProductDescription -> ProductDescriptionLoaded(result as ProductDescription)
            is RestoreLastState -> LastViewStateRestored(action.lastViewState)
        }
    }

    fun errorOf(action: ProductViewAction, error: Throwable): ProductPartialState {
        return when (action) {
            is LoadProductDetail -> StateError(error, action.productId)
            else -> StateError(error)
        }
    }
}
