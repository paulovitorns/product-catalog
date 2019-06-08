package br.com.productcatalog.screens.splash

import br.com.productcatalog.R
import br.com.productcatalog.screens.BaseActivity
import br.com.productcatalog.screens.BaseUi

interface SplashUi : BaseUi

class SplashActivity : BaseActivity<SplashPresenter>(), SplashUi {

    override val layoutRes: Int? = R.layout.activity_splash
}
