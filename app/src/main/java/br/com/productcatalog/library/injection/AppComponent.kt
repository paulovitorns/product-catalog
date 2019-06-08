package br.com.productcatalog.library.injection

import android.app.Application
import br.com.productcatalog.ProductCatalogApplication
import br.com.productcatalog.library.injection.modules.ActivityBindingModule
import br.com.productcatalog.library.injection.modules.AppModule
import br.com.productcatalog.library.injection.modules.FragmentBindingModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@Component(
    modules = [
        AppModule::class,
        ActivityBindingModule::class,
        FragmentBindingModule::class,
        AndroidSupportInjectionModule::class
    ]
)
interface AppComponent : AndroidInjector<ProductCatalogApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}
