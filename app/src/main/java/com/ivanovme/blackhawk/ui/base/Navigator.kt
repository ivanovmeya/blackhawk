package com.ivanovme.blackhawk.ui.base

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.maps.model.LatLng
import com.ivanovme.blackhawk.R
import com.ivanovme.blackhawk.ui.map.MapFragment
import com.ivanovme.blackhawk.ui.route.RouteFragment
import com.ivanovme.blackhawk.ui.search.SearchFragment

class Navigator(private val activityHolder: ActivityHolder) {

    private val searchFragmentTag = "search_fragment"
    private val routeFragmentTag = "route_fragment"
    private val mapFragmentTag = "map_fragment"
    private val mapState = "map_State"
    private val searchState = "search_state"

    fun showSearch(isDeparture: Boolean) {
        val fragment = SearchFragment.newInstance(isDeparture)
        getSupportFragmentManager().beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(R.id.container, fragment, searchFragmentTag)
            .addToBackStack(searchState)
            .commit()
    }

    fun showMap(departure: LatLng, depAirport: String, destination: LatLng, destAirport: String) {
        getSupportFragmentManager().beginTransaction()
            .replace(
                R.id.container,
                MapFragment.newInstance(departure, depAirport, destination, destAirport),
                mapFragmentTag
            )
            .addToBackStack(mapState)
            .commit()
    }

    fun showRoute() {
        val routeFragment = getSupportFragmentManager().findFragmentByTag(routeFragmentTag)
            ?: RouteFragment.newInstance()

        val fm = getSupportFragmentManager()
        fm.popBackStackImmediate()

        fm.beginTransaction()
            .replace(R.id.container, routeFragment)
            .commit()
    }

    private fun getSupportFragmentManager(): FragmentManager =
        activityHolder.getActivity()?.supportFragmentManager
            ?: throw Exception("Activity Holder is not initialized with activity")

}