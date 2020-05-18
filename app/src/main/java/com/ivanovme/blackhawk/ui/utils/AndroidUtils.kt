package com.ivanovme.blackhawk.ui.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.ivanovme.blackhawk.ui.base.RxLifecycleObserver
import io.reactivex.rxjava3.disposables.CompositeDisposable

fun Fragment.withLifecycle(setup: (CompositeDisposable) -> Unit) {
    this.viewLifecycleOwner.lifecycle.addObserver(RxLifecycleObserver(setup))
}

fun Context.hideKeyboard() {
    try {
        if ((this as Activity).currentFocus != null &&
            this.currentFocus!!.windowToken != null
        ) {
            (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(
                    this.currentFocus?.windowToken,
                    0
                )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.showKeyboard() {
    if (this.requestFocus()) {
        val imm =
            this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()