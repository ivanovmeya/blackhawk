package com.ivanovme.blackhawk.network.model

data class CityResponse(
    val country: String,
    val location: Location,
    val iata: List<String>,
    val city: String
)

data class Location(val lat: Double, val lon: Double)