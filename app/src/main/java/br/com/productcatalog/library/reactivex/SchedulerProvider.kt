package br.com.productcatalog.library.reactivex

import io.reactivex.Scheduler

interface SchedulerProvider {
    fun workerThread(): Scheduler
    fun postWorkerThread(): Scheduler
}
