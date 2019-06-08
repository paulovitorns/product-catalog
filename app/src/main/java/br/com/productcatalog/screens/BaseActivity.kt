package br.com.productcatalog.screens

import android.os.Bundle
import dagger.android.DaggerActivity
import javax.inject.Inject

interface BaseUi

abstract class BaseActivity<T : BasePresenter<BaseUi>> : DaggerActivity(), BaseUi {

    @Inject
    lateinit var presenter: T

    abstract val layoutRes: Int?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutRes?.let { setContentView(it) }
        setupViews()
        presenter.setUi(this)
        presenter.onCreate()
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onRestart() {
        super.onRestart()
        presenter.setUi(this)
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onStop() {
        presenter.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    open fun setupViews() {}
}
