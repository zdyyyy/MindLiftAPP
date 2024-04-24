package com.mindlift.android.mapFeature.model

data class PlacesResponse(
    val results: List<Place>
)

data class Place(
    val id: String?,
    val name: String,
    val geometry: Geometry
)

data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)
