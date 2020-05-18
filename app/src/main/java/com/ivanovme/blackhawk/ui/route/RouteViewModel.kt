package com.ivanovme.blackhawk.ui.route

import com.ivanovme.blackhawk.ui.base.BaseViewModel

class RouteViewModel(
    private val routeHolder: RouteHolder
) : BaseViewModel() {
    fun getRouteUpdates() = routeHolder.getRouteUpdates()
}