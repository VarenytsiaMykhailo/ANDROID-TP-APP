package com.example.app.businesslayer.providers

import com.example.app.datalayer.models.Place
import com.example.app.datalayer.models.PlaceDescription
import com.example.app.datalayer.repositories.MapRepository

internal class MapProvider {

    private val mapRepository = MapRepository.mapRepository

    suspend fun getPlaces(): List<Place> =
        mapRepository.getPlaces()

    suspend fun getPlaceDescription(placeId: String): PlaceDescription =
        mapRepository.getPlaceDescription(placeId)
}