package br.com.productcatalog.library.reactivex

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class TestSchedulerProvider : SchedulerProvider {

    override fun workerThread(): Scheduler = Schedulers.trampoline()
    override fun postWorkerThread(): Scheduler = Schedulers.trampoline()
}
