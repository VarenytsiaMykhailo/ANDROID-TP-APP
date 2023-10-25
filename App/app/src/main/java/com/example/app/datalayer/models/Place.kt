package com.example.app.datalayer.models

import com.google.gson.annotations.SerializedName

internal data class Place(

    @SerializedName("id")
    val id: String,

    @SerializedName("place_id")
    val placeId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("cover")
    val imageUrl: String,

    @SerializedName("rating")
    val rating: Double,

    @SerializedName("rating_count")
    val ratingCount: Double,

    @SerializedName("location")
    val location: Location,
) {

    data class Location(
        @SerializedName("lat")
        val lat: Double,

        @SerializedName("lng")
        val lng: Double,
    )
}
