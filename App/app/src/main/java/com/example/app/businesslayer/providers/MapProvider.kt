package com.example.app.businesslayer.providers

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
     */
    suspend fun getSuggestPlaces(
        lat: Double,
        lng: Double,
        radius: Int
    ): List<Place> =
        mapRepository.getSuggestPlaces("$lat,$lng", radius.toString())

    /**
     * @param placeId Example: "ChIJfRJDflpKtUYRl0UbgcrmUUk".
     */
    suspend fun getPlaceDescription(placeId: String): PlaceDescription =
        mapRepository.getPlaceDescription(placeId)
}