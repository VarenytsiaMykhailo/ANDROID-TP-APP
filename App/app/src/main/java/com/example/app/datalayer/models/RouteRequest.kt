package com.example.app.datalayer.models

import com.google.gson.annotations.SerializedName

data class RouteRequest (

    @SerializedName("travel_mode")
    val travelMode: TravelMode,

    @SerializedName("start")
    val start: Location,

    @SerializedName("end")
    val end: Location,

    @SerializedName("waypoints")
    val waypoints: List<Waypoint>,
) {

    enum class TravelMode {
        @SerializedName("WALK")
        WALK,
        @SerializedName("DRIVE")
        DRIVE,
        @SerializedName("BICYCLE")
        BICYCLE,
        @SerializedName("TWO_WHEELER")
        TWO_WHEELER,
        @SerializedName("TRANSIT")
        TRANSIT,
    }

    data class Location(
        @SerializedName("lat")
        val lat: Double,

        @SerializedName("lng")
        val lng: Double,
    )

    data class Waypoint(
        @SerializedName("place_id")
        val placeId: String,

        @SerializedName("location")
        val location: Location,
    )
}