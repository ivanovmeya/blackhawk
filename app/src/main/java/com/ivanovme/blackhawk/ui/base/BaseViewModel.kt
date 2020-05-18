package com.ivanovme.blackhawk.ui.base

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.koin.core.KoinComponent

open class BaseViewModel : ViewModel(), KoinComponent {

    protected val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}