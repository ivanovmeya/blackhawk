package com.ivanovme.blackhawk.ui.base

import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference

class ActivityHolder {
    private var activityRef: WeakReference<AppCompatActivity>? = null

    fun setActivity(activity: AppCompatActivity) {
        activityRef = WeakReference(activity)
    }

    fun getActivity(): AppCompatActivity? = activityRef?.get()

    fun clear() {
        activityRef = null
    }

}