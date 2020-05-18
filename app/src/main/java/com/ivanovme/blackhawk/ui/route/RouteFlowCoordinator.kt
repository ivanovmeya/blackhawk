package com.ivanovme.blackhawk.ui.route

import com.google.android.gms.maps.model.LatLng
import com.ivanovme.blackhawk.ui.base.Navigator

class RouteFlowCoordinator(private val navigator: Navigator) {

    fun showRoute() {
        navigator.showRoute()
    }

    fun selectDeparture() {
        navigator.showSearch(true)
    }

    fun selectDestination() {
        navigator.showSearch(false)
    }

    fun showMap(
        departure: LatLng,
        depAirportCode: String,
        destination: LatLng,
        destAirportCode: String
    ) {
        navigator.showMap(departure, depAirportCode, destination, destAirportCode)
    }
}