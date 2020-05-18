package com.ivanovme.blackhawk.ui.main

import com.ivanovme.blackhawk.ui.base.Navigator

class MainFlowCoordinator(private val navigator: Navigator) {
    fun showRouteFeature() {
        navigator.showRoute()
    }
}