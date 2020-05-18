package com.ivanovme.blackhawk.ui.search.data

import com.google.android.gms.maps.model.LatLng

data class CityData(
    val city: String,
    val country: String,
    val airportCode: String,
    val location: LatLng
)