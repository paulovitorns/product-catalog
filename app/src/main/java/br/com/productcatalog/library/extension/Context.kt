package br.com.productcatalog.library.extension

import android.content.Context
import android.net.ConnectivityManager

fun Context.getConnectivityManager(): ConnectivityManager =
    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager