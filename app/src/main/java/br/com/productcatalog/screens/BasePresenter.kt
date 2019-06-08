package br.com.productcatalog.screens

abstract class BasePresenter<T : BaseUi> {

    private var ui: T? = null

    fun setUi(ui: T?) {
        this.ui = ui
    }

    @Suppress("UNCHECKED_CAST")
    fun <I : BaseUi> baseUi(): I? = ui as I?

    open fun onCreate() {}

    open fun onStart() {}

    open fun onResume() {}

    open fun onPause() {}

    open fun onStop() {}

    open fun onDestroy() {
        ui = null
    }
}
