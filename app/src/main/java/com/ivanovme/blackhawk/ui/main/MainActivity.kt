package com.ivanovme.blackhawk.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ivanovme.blackhawk.R
import com.ivanovme.blackhawk.ui.base.ActivityHolder
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val activityHolder: ActivityHolder by inject()
    private val mainFlowCoordinator: MainFlowCoordinator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activityHolder.setActivity(this)

        if (savedInstanceState == null) {
            mainFlowCoordinator.showRouteFeature()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        activityHolder.clear()
    }
}