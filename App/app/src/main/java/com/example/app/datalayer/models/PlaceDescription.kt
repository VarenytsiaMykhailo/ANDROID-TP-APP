package com.example.app.datalayer.models

import com.google.gson.annotations.SerializedName

internal data class PlaceDescription(

    @SerializedName("id")
    val id: String,

    @SerializedName("place_id")
    val placeId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("rating")
    val rating: Double,

    @SerializedName("rating_count")
    val ratingCount: Int,

    @SerializedName("location")
    val location: Location,

    @SerializedName("description")
    val description: String,

    @SerializedName("photos")
    val photos: List<String>,

    @SerializedName("address")
    val address: String,

    @SerializedName("workingHours")
    val workingHours: List<String>,

    @SerializedName("tags")
    val tags: List<String>,
) {
    data class Location(
        @SerializedName("lat")
        val lat: Double,

        @SerializedName("lng")
        val lng: Double,
    )
}