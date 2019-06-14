package br.com.productcatalog.library.injection.modules

import android.content.Context
import br.com.productcatalog.R
import br.com.productcatalog.data.product.DefaultProductDetailRepository
import br.com.productcatalog.domain.product.ProductDetailRepository
import br.com.productcatalog.library.injection.scope.ActivityScope
import br.com.productcatalog.library.retrofit.RetrofitFactory
import br.com.productcatalog.library.retrofit.endpoint.ProductDetailEndpoint
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(includes = [ProductRepositoryModule::class])
class ProductDetailModule {

    @ActivityScope
    @Provides
    fun provideProductService(
        context: Context,
        retrofitFactory: RetrofitFactory
    ): ProductDetailEndpoint {
        val baseUrl = context.getString(R.string.base_url)
        return retrofitFactory.create(ProductDetailEndpoint::class.java, baseUrl)
    }
}

@Module
abstract class ProductRepositoryModule {

    @ActivityScope
    @Binds
    abstract fun bindDefaultProductRepository(
        defaultProductDetailRepository: DefaultProductDetailRepository
    ): ProductDetailRepository
}
