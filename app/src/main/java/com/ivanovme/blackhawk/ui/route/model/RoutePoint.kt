package com.ivanovme.blackhawk.ui.route.model

import com.google.android.gms.maps.model.LatLng

data class RoutePoint(val city: String, val airportCode: String, val location: LatLng)