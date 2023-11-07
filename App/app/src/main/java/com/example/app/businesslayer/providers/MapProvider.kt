package com.example.app.businesslayer.providers

import android.util.Log
import com.example.app.datalayer.models.NearbyPlace
import com.example.app.datalayer.models.Place
import com.example.app.datalayer.models.PlaceDescription
import com.example.app.datalayer.repositories.MapRepository

object MapProvider {

    private val mapRepository = MapRepository.mapRepository
    private var updateListFlag = true
    var radius = 3000

    lateinit var placesList: MutableList<NearbyPlace>

    suspend fun getPlaces(): List<Place> =
        mapRepository.getPlaces()

    /**
     * @param lat Example: 55.753544.
     * @param lng Example: 37.621202.
     * @param radius Example: 50000 in metres.
     * @param limit Example: 10.
     * @param offset Example: 10.
     */
    suspend fun getSuggestPlaces(
        lat: Double,
        lng: Double,
        limit: Int,
        offset: Int,
    ): List<NearbyPlace> {
        if (updateListFlag) {
            placesList = mapRepository.getSuggestPlaces(
                "$lat,$lng",
                radius.toString(),
                limit,
                offset,
            ).also {
            }.toMutableList()
            updateListFlag = false
        }
        return placesList
    }

    fun updateRadius(rad: Int) {
        radius = rad
        updateListFlag = true
    }

    /**
     * @param placeId Example: "ChIJfRJDflpKtUYRl0UbgcrmUUk".
     */
    suspend fun getPlaceDescription(placeId: String): PlaceDescription =
        mapRepository.getPlaceDescription(placeId).also {
        }


}