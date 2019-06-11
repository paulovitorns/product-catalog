package br.com.productcatalog.library.injection.modules

import android.content.Context
import br.com.productcatalog.R
import br.com.productcatalog.data.search.DefaultSearchRepository
import br.com.productcatalog.domain.search.SearchRepository
import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.library.retrofit.RetrofitFactory
import br.com.productcatalog.library.retrofit.endpoint.SearchEndpoint
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(includes = [SearchRepositoryModule::class])
class SearchModule {

    @ActivityScope
    @Provides
    fun provideProductService(
        context: Context,
        retrofitFactory: RetrofitFactory
    ): SearchEndpoint {
        val baseUrl = context.getString(R.string.base_url)
        return retrofitFactory.create(SearchEndpoint::class.java, baseUrl)
    }
}

@Module
abstract class SearchRepositoryModule {

    @ActivityScope
    @Binds
    abstract fun bindDefaultSearchRepository(defaultSearchRepository: DefaultSearchRepository): SearchRepository
}
