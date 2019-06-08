package br.com.productcatalog.library.injection.modules

import android.app.Application
import android.content.Context
import br.com.productcatalog.library.reactivex.DefaultSchedulerProvider
import br.com.productcatalog.library.reactivex.SchedulerProvider
import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module
abstract class AppModule {

    @Binds
    abstract fun bindContext(application: Application): Context

    @Reusable
    @Binds
    abstract fun defaultSchedulerProvider(
        defaultSchedulerProvider: DefaultSchedulerProvider
    ): SchedulerProvider
}
