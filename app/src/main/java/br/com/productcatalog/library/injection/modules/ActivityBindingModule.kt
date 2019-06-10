package br.com.productcatalog.library.injection.modules

import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.screens.home.HomeActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun dashboardActivity(): HomeActivity
}
