package com.example.app.datalayer.models

import com.example.app.datalayer.repositories.LocalPropertiesSecretsRepository
import com.google.gson.annotations.SerializedName

data class PlaceDescription(

    @SerializedName("place_id")
    val placeId: String,

    @SerializedName("name")
    private val _name: String?,

    @SerializedName("rating")
    private val _rating: Double?,

    @SerializedName("rating_count")
    private val _ratingCount: Int?,

    @SerializedName("description")
    private val _description: String?,

    @SerializedName("address")
    private val _address: String?,

    @SerializedName("workingHours")
    private val _workingHours: List<String>?,

    @SerializedName("tags")
    private val _tags: List<String>?,

    @SerializedName("photos")
    private val _photos: List<String>?,

    @SerializedName("location")
    val location: Location,

    @SerializedName("reaction")
    val _reactions: List<String>?,
) {

    val name: String
        get() = _name ?: "Название отсутствует"

    val rating: Double
        get() = _rating ?: 0.0

    val ratingCount: Int
        get() = _ratingCount ?: 0

    val description: String
        get() = _description ?: "Описание отсутствует"

    val address: String
        get() = _address ?: "Адрес отсутствует"

    val workingHours: List<String>
        get() =
            if (!_workingHours.isNullOrEmpty()) {
                _workingHours
            } else {
                listOf("График работы отсутствует")
            }

    val tags: List<String>
        get() = _tags ?: emptyList()

    val photos: List<String>
        get() =
            if (!_photos.isNullOrEmpty()) {
                _photos
            } else {
                listOf(LocalPropertiesSecretsRepository.defaultImageUrl)
            }
    val reactions: List<String>
        get() =
            if (!_reactions.isNullOrEmpty()) {
                _reactions
            } else {
                listOf("nothing")
            }

    data class Location(
        @SerializedName("lat")
        val lat: Double,

        @SerializedName("lng")
        val lng: Double,
    )

}