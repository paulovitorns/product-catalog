package br.com.productcatalog.library.reactivex

import io.reactivex.CompletableTransformer
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

/**
 * Applies the schedulers to a observable source without change the upstream Observable.
 * @param schedulerProvider the SchedulerProvider instance that contains the workerThread and the postWorkerThread
 * @return the transformed ObservableSource instance
 */
fun <T> applyObservableSchedulers(schedulerProvider: SchedulerProvider): ObservableTransformer<T, T> {
    return ObservableTransformer { observer ->
        observer.flatMap { Observable.just(it) }
            .subscribeOn(schedulerProvider.workerThread())
            .observeOn(schedulerProvider.postWorkerThread())
    }
}

fun applyCompletableSchedulers(schedulerProvider: SchedulerProvider): CompletableTransformer {
    return CompletableTransformer { completable ->
        completable
            .subscribeOn(schedulerProvider.workerThread())
            .observeOn(schedulerProvider.postWorkerThread())
    }
}