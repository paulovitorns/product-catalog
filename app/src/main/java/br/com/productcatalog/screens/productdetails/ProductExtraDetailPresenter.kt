package br.com.productcatalog.screens.productdetails

import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.library.state.StateStore
import br.com.productcatalog.screens.BasePresenter
import br.com.productcatalog.screens.BaseUi
import javax.inject.Inject

@ActivityScope
class ProductExtraDetailPresenter @Inject constructor(
    private val stateStore: StateStore
) : BasePresenter<BaseUi>() {

    private val productExtraDetailUi: ProductExtraDetailUi? get() = baseUi()
    private var lastState: ProductExtraDetailsAction? = null

    override fun onCreate() {
        super.onCreate()
        val data: ProductExtraDetailsAction? = stateStore.load(ProductExtraDetailUi::class)
        lastState = data
        when (data) {
            is ProductExtraDetailsAction.OpenCharacteristics -> {
                productExtraDetailUi?.showTitle(data.productDetail.title)
                productExtraDetailUi?.showCharacteristics(data.productDetail.characteristics ?: emptyList())
            }
            is ProductExtraDetailsAction.OpenDescription -> {
                productExtraDetailUi?.showTitle(data.productDetail.title)
                val fullText = if (data.productDetail.description?.text?.isNotEmpty() == true) {
                    data.productDetail.description!!.text + data.productDetail.description!!.plainText
                } else {
                    data.productDetail.description!!.plainText
                }
                productExtraDetailUi?.showFullDescription(fullText)
            }
            else -> {
                // nothing to do here
            }
        }
    }

    override fun onSaveState() {
        super.onSaveState()
        lastState?.let {
            stateStore.save(ProductExtraDetailUi::class, it)
        }
    }
}
