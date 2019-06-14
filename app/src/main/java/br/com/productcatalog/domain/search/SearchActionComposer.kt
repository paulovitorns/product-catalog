package br.com.productcatalog.domain.search

import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.screens.search.PartialStateChanged
import br.com.productcatalog.screens.search.SearchViewAction
import br.com.productcatalog.screens.search.SearchViewAction.LoadNextPage
import br.com.productcatalog.screens.search.SearchViewAction.OpenProductDetail
import br.com.productcatalog.screens.search.SearchViewAction.RestoreLastState
import br.com.productcatalog.screens.search.SearchViewAction.SearchProduct
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import javax.inject.Inject

@ActivityScope
class SearchActionComposer @Inject constructor(
    private val mlbSearchProduct: MlbSearchProductUseCase,
    private val loadNextPage: LoadNextPageUseCase,
    private val searchResultMapper: SearchResultMapper
) {

    fun bindActions(): ObservableTransformer<SearchViewAction, PartialStateChanged> {
        return ObservableTransformer { action ->
            action.publish { shared ->
                Observable.mergeArray(
                    shared.ofType(SearchProduct::class.java).compose(searchProductAction()),
                    shared.ofType(LoadNextPage::class.java).compose(loadNexPageAction()),
                    shared.ofType(RestoreLastState::class.java).compose(restoreLastStateAction()),
                    shared.ofType(OpenProductDetail::class.java).compose(openProductDetailsAction())
                )
            }
        }
    }

    private fun searchProductAction(): ObservableTransformer<SearchProduct, PartialStateChanged> {
        return ObservableTransformer { observer ->
            observer.flatMap { action ->
                mlbSearchProduct(action.queryString)
                    .map { result -> searchResultMapper.stateOf(action, result) }
                    .onErrorReturn { searchResultMapper.errorOf(action, it) }
                    .startWith(PartialStateChanged.Loading)
            }
        }
    }

    private fun loadNexPageAction(): ObservableTransformer<LoadNextPage, PartialStateChanged> {
        return ObservableTransformer { observer ->
            observer.flatMap { action ->
                loadNextPage(action.lastPage)
                    .map { result -> searchResultMapper.stateOf(action, result) }
                    .onErrorReturn { searchResultMapper.errorOf(action, it) }
                    .startWith(PartialStateChanged.Loading)
            }
        }
    }

    private fun restoreLastStateAction(): ObservableTransformer<RestoreLastState, PartialStateChanged> {
        return ObservableTransformer { observer ->
            observer.flatMap { action ->
                Observable.just(action)
                    .map { searchResultMapper.stateOf(action, it) }
                    .onErrorReturn { searchResultMapper.errorOf(action, it) }
                    .startWith(PartialStateChanged.Loading)
            }
        }
    }

    private fun openProductDetailsAction(): ObservableTransformer<OpenProductDetail, PartialStateChanged> {
        return ObservableTransformer { observer ->
            observer.flatMap { action ->
                Observable.just(action)
                    .map { searchResultMapper.stateOf(action, action.productResult) }
                    .onErrorReturn { searchResultMapper.errorOf(action, it) }
                    .startWith(PartialStateChanged.Loading)
            }
        }
    }
}
