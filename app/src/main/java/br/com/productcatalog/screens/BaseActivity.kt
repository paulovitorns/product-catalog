package br.com.productcatalog.screens

import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

interface BaseUi

abstract class BaseActivity<T : BasePresenter<BaseUi>> : DaggerAppCompatActivity(), BaseUi {

    @Inject
    lateinit var presenter: T

    abstract val layoutRes: Int?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutRes?.let { setContentView(it) }
        setupToolbar()
        setupViews()
        presenter.setUi(this)
        presenter.onCreate()
    }

    override fun onRestart() {
        super.onRestart()
        presenter.setUi(this)
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        presenter.onSaveState()
        super.onSaveInstanceState(outState)
    }

    open fun setupToolbar() {}

    open fun setupViews() {}
}
