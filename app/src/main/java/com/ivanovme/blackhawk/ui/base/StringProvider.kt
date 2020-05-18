package com.ivanovme.blackhawk.ui.base

import android.content.Context
import androidx.annotation.StringRes

class StringProvider(private val context: Context) {
    fun get(@StringRes stringId: Int): String = context.getString(stringId)
}