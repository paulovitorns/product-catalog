package br.com.productcatalog.screens.product

import br.com.productcatalog.R
import br.com.productcatalog.screens.BaseActivity
import br.com.productcatalog.screens.BaseUi

interface ProductUi : BaseUi

class ProductActivity : BaseActivity<ProductPresenter>(), ProductUi {

    override val layoutRes: Int? = R.layout.product_layout
}
