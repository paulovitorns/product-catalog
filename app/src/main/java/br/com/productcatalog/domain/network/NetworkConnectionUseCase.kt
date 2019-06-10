package br.com.productcatalog.domain.network

import android.content.Context
import br.com.productcatalog.library.extension.getConnectivityManager
import br.com.productcatalog.library.reactivex.SchedulerProvider
import br.com.productcatalog.library.reactivex.applyCompletableSchedulers
import io.reactivex.Completable
import java.net.InetAddress
import java.net.UnknownHostException
import javax.inject.Inject

class NetworkConnectionUseCase @Inject constructor(
    context: Context,
    private val schedulerProvider: SchedulerProvider
) {
    // Yep, this will not work on China
    private val pingUrl = "www.google.com"
    private val connectivityManager = context.getConnectivityManager()

    operator fun invoke(): Completable {
        return Completable.fromAction {
            if (!isNetworkConnected() && !isInternetAvailable()) {
                throw IllegalStateException("No valid network info is available")
            }
        }.compose(applyCompletableSchedulers(schedulerProvider))
    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val inetAddress = InetAddress.getByName(pingUrl)
            inetAddress.toString().isNotBlank()
        } catch (e: UnknownHostException) {
            false
        }
    }

    private fun isNetworkConnected(): Boolean {
        return connectivityManager.activeNetworkInfo != null &&
                connectivityManager.activeNetworkInfo.isConnectedOrConnecting
    }
}
