package br.com.productcatalog.screens.product

import android.util.Log
import br.com.productcatalog.data.models.ProductResult
import br.com.productcatalog.domain.product.ProductDetailComposer
import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.library.reactivex.SchedulerProvider
import br.com.productcatalog.library.reactivex.addDisposableTo
import br.com.productcatalog.library.reactivex.applyObservableSchedulers
import br.com.productcatalog.library.reactivex.withDelay
import br.com.productcatalog.library.state.StateStore
import br.com.productcatalog.screens.BasePresenter
import br.com.productcatalog.screens.BaseUi
import br.com.productcatalog.screens.productdetails.ProductExtraDetailUi
import br.com.productcatalog.screens.productdetails.ProductExtraDetailsAction
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.net.UnknownHostException
import javax.inject.Inject

@ActivityScope
class ProductPresenter @Inject constructor(
    private val productDetailComposer: ProductDetailComposer,
    private val schedulerProvider: SchedulerProvider,
    private val stateStore: StateStore
) : BasePresenter<BaseUi>() {

    private val productUi: ProductUi? get() = baseUi()
    private val publishSubject: PublishSubject<ProductViewAction> = PublishSubject.create()
    private var lastState: ProductViewState? = null

    override fun onCreate() {
        super.onCreate()
        bindIntents()
        withDelay(300, schedulerProvider.workerThread()) {
            loadProductOrRestoreLastState()
        }
    }

    override fun onSaveState() {
        super.onSaveState()
        lastState?.let {
            stateStore.save(ProductUi::class, it)
        }
    }

    private fun bindIntents() {

        val initialState = ProductViewState()

        publishSubject
            .compose(productDetailComposer.bindActions())
            .compose(applyObservableSchedulers(schedulerProvider))
            .scan(initialState, this::viewStateReducer)
            .distinctUntilChanged()
            .doOnNext { result ->
                productUi?.render(result)
            }
            .subscribe({ result ->
                lastState = result

                if (result.productDetail != null && result.productDetail.description == null && result.stateError == null) {
                    publishSubject.onNext(ProductViewAction.LoadProductDescription(result.productDetail.id))
                    return@subscribe
                }

                if (result.isShowFullCharacteristics) {
                    stateStore.save(
                        ProductExtraDetailUi::class,
                        ProductExtraDetailsAction.OpenCharacteristics(result.productDetail!!)
                    )
                    return@subscribe
                }

                if (result.isShowFullDescription) {
                    stateStore.save(
                        ProductExtraDetailUi::class,
                        ProductExtraDetailsAction.OpenDescription(result.productDetail!!)
                    )
                    return@subscribe
                }
            }, { error ->
                Log.e("SEARCH", error.message)
            }).addDisposableTo(disposeBag)

        val retryIntent: Observable<ProductViewAction> = productUi?.retryButton()!!
            .filter { lastState?.stateError is UnknownHostException }
            .map {
                return@map when {
                    lastState?.productToOpen != null ->
                        ProductViewAction.LoadProductDetail(lastState?.productToOpen!!)
                    lastState?.productDetail?.description == null ->
                        ProductViewAction.LoadProductDescription(lastState?.productDetail?.id!!)
                    else -> ProductViewAction.RestoreLastState(lastState!!)
                }
            }

        val openCharacteristicIntent: Observable<ProductViewAction> = productUi?.openMoreCharacteristics()!!
            .map { ProductViewAction.OpenFullCharacteristics(lastState?.productDetail!!) }

        val openDescriptionIntent: Observable<ProductViewAction> = productUi?.openFullDescription()!!
            .map { ProductViewAction.OpenFullDescription(lastState?.productDetail!!) }

        val allIntents: Observable<ProductViewAction> = Observable.merge(
            retryIntent,
            openCharacteristicIntent,
            openDescriptionIntent
        )

        allIntents.subscribe(publishSubject)
    }

    private fun loadProductOrRestoreLastState() {
        when (val param: Any? = stateStore.load(ProductUi::class)) {
            is ProductResult -> {
                publishSubject.onNext(ProductViewAction.LoadProductDetail(param))
            }
            is ProductViewState -> publishSubject.onNext(ProductViewAction.RestoreLastState(param))
            else -> {
                // nothing to do here
            }
        }
    }

    private fun viewStateReducer(
        previousState: ProductViewState,
        partialChanges: ProductPartialState
    ): ProductViewState {
        return when (partialChanges) {
            is ProductPartialState.Loading -> {
                previousState.builder()
                    .setStateError(null)
                    .setLoading(true)
                    .build()
            }
            is ProductPartialState.StateError -> {
                previousState.builder()
                    .setProductToOpen(partialChanges.productToOpen)
                    .setLoading(false)
                    .setStateError(partialChanges.error)
                    .build()
            }
            is ProductPartialState.ProductDetailLoaded -> {
                previousState.builder()
                    .setProductToOpen(null)
                    .setLoading(false)
                    .setStateError(null)
                    .setProductPresentation(true)
                    .setDescriptionPresentation(false)
                    .setProductDetail(partialChanges.productDetail)
                    .build()
            }
            is ProductPartialState.ProductDescriptionLoaded -> {
                previousState.builder()
                    .setLoading(false)
                    .setStateError(null)
                    .setProductPresentation(!previousState.isDescriptionPresentation)
                    .setDescriptionPresentation(true)
                    .setProductDetail(previousState.productDetail?.apply {
                        description = partialChanges.productDescription
                    })
                    .build()
            }
            is ProductPartialState.LastViewStateRestored -> {
                previousState.builder()
                    .setLoading(false)
                    .setStateError(partialChanges.lastViewState.stateError)
                    .setProductPresentation(partialChanges.lastViewState.productDetail != null)
                    .setDescriptionPresentation(partialChanges.lastViewState.productDetail?.description != null)
                    .setProductDetail(partialChanges.lastViewState.productDetail)
                    .build()
            }
            is ProductPartialState.FullCharacteristicsOpened -> {
                previousState.builder()
                    .setLoading(false)
                    .setStateError(null)
                    .setShowFullCharacteristics(true)
                    .build()
            }
            is ProductPartialState.FullDescriptionOpened -> {
                previousState.builder()
                    .setLoading(false)
                    .setStateError(null)
                    .setShowFullCharacteristics(false)
                    .setShowFullDescription(true)
                    .build()
            }
        }
    }
}
