package com.example.app.datalayer.models

import com.google.gson.annotations.SerializedName

data class RouteResponse(
    @SerializedName("travel_mode")
    val travelMode: String,

    @SerializedName("route")
    val route: List<Polyline>,
) {

    data class Polyline(
        @SerializedName("polyline")
        val polyline: String,
    )
}