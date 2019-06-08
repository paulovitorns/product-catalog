package br.com.productcatalog.screens.splash

import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.screens.BasePresenter
import br.com.productcatalog.screens.BaseUi
import javax.inject.Inject

@ActivityScope
class SplashPresenter @Inject constructor() : BasePresenter<BaseUi>()
