package com.example.app.businesslayer.providers

import android.util.Log
import com.example.app.datalayer.models.NearbyPlace
import com.example.app.datalayer.models.Place
import com.example.app.datalayer.models.PlaceDescription
import com.example.app.datalayer.repositories.MapRepository

internal class MapProvider {

    private val mapRepository = MapRepository.mapRepository

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
        radius: Int,
        limit: Int,
        offset: Int,
    ): List<NearbyPlace> =
        mapRepository.getSuggestPlaces(
            "$lat,$lng",
            radius.toString(),
            limit,
            offset,
        ).also {
            Log.d(LOG_TAG, "getSuggestPlaces = $it")
        }

    /**
     * @param placeId Example: "ChIJfRJDflpKtUYRl0UbgcrmUUk".
     */
    suspend fun getPlaceDescription(placeId: String): PlaceDescription =
        mapRepository.getPlaceDescription(placeId).also {
            Log.d(LOG_TAG, "getPlaceDescription = $it")
        }

    companion object {

        private const val LOG_TAG = "MapProvider"
    }
}