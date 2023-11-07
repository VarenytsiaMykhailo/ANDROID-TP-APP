package com.example.app.datalayer.models

import com.example.app.R
import com.example.app.datalayer.repositories.LocalPropertiesSecretsRepository
import com.google.gson.annotations.SerializedName

data class NearbyPlace(

    @SerializedName("place_id")
    val placeId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("cover")
    private val _mainImageUrl: String?,

    @SerializedName("photos")
    val photos: List<String>,

    @SerializedName("rating")
    val rating: Double,

    @SerializedName("rating_count")
    val ratingCount: Int,

    @SerializedName("reaction")
    val reaction: String,

    @SerializedName("location")
    val location: Location,
) {

    val mainImageUrl: String
        get() = _mainImageUrl
            ?: (
                    "android.resource://" +
                            LocalPropertiesSecretsRepository.APP_PACKAGE_NAME +
                            "/" +
                            R.mipmap.there_is_no_image
                    )

    data class Location(
        @SerializedName("lat")
        val lat: Double,

        @SerializedName("lng")
        val lng: Double,
    )


}