package com.example.app.datalayer.models

import com.google.gson.annotations.SerializedName

data class SortPlaceResponse (

    @SerializedName("start")
    val start: Location,

    @SerializedName("end")
    val end: Location,

    @SerializedName("waypoints")
    val waypoints: List<SortPlacesRequest.Location>,
) {

    data class Location(
        @SerializedName("lat")
        val lat: Double,

        @SerializedName("lng")
        val lng: Double,
    )

    data class Waypoint(
        @SerializedName("location")
        val location: Location,
    )
}