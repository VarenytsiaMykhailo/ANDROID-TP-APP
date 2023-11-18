package com.example.app.datalayer.models

import com.example.app.datalayer.repositories.LocalPropertiesSecretsRepository
import com.google.gson.annotations.SerializedName

data class NearbyPlace(

    @SerializedName("place_id")
    var placeId: String,

    @SerializedName("name")
    private val _name: String?,

    @SerializedName("cover")
    private val _mainImageUrl: String?,

    @SerializedName("rating")
    private val _rating: Double?,

    @SerializedName("rating_count")
    private val _ratingCount: Int?,

    @SerializedName("reaction")
    val reactions: List<String>,

    @SerializedName("location")
    val location: PlaceDescription.Location,
) {

    val name: String
        get() = _name ?: "Название отсутствует"

    val mainImageUrl: String
        get() = _mainImageUrl
            ?: LocalPropertiesSecretsRepository.defaultImageUrl

    val rating: Double
        get() = _rating ?: 0.0

    val ratingCount: Int
        get() = _ratingCount ?: 0


    data class Location(
        @SerializedName("lat")
        val lat: Double,

        @SerializedName("lng")
        val lng: Double,
    )
}