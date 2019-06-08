package br.com.productcatalog.library.reactivex

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.SingleTransformer

/**
 * Applies the schedulers to a single source without change the upstream Single.
 * @param schedulerProvider the SchedulerProvider instance that contains the workerThread and the postWorkerThread
 * @return the transformed SingleSource instance
 */
fun <T> applySingleSchedulers(schedulerProvider: SchedulerProvider): SingleTransformer<T, T> {
    return SingleTransformer { single ->
        single.flatMap {
            Single.just(it)
                .subscribeOn(schedulerProvider.workerThread())
                .observeOn(schedulerProvider.postWorkerThread())
        }
    }
}

/**
 * Applies the schedulers to a observable source without change the upstream Observable.
 * @param schedulerProvider the SchedulerProvider instance that contains the workerThread and the postWorkerThread
 * @return the transformed ObservableSource instance
 */
fun <T> applyObservableSchedulers(schedulerProvider: SchedulerProvider): ObservableTransformer<T, T> {
    return ObservableTransformer { single ->
        single.flatMap {
            Observable.just(it)
                .subscribeOn(schedulerProvider.workerThread())
                .observeOn(schedulerProvider.postWorkerThread())
        }
    }
}
