package br.com.productcatalog.screens.product

import br.com.productcatalog.library.state.StateStore
import br.com.productcatalog.screens.BasePresenter
import br.com.productcatalog.screens.BaseUi
import javax.inject.Inject

class ProductPresenter @Inject constructor(
    private val stateStore: StateStore
) : BasePresenter<BaseUi>() {

    override fun onCreate() {
        super.onCreate()
        stateStore
    }
}