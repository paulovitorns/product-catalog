package br.com.productcatalog.library.injection.modules

import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.screens.home.HomeActivity
import br.com.productcatalog.screens.search.SearchActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun dashboardActivity(): HomeActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [SearchModule::class])
    abstract fun searchActivity(): SearchActivity
}
