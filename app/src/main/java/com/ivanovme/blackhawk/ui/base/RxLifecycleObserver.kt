package com.ivanovme.blackhawk.ui.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.rxjava3.disposables.CompositeDisposable

class RxLifecycleObserver(val setup: (CompositeDisposable) -> Unit) : LifecycleObserver {
    private var compositeDisposable = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        setup(compositeDisposable)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        compositeDisposable.dispose()
        compositeDisposable = CompositeDisposable()
    }
}