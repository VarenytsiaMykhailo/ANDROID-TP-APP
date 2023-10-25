package com.example.app.businesslayer.providers

import com.example.app.datalayer.models.Place
import com.example.app.datalayer.repositories.MapRepository

internal class MapProvider {

    private val mapRepository = MapRepository.mapRepository

    suspend fun getPlaces(): List<Place> {
        return mapRepository.getPlaces()
    }
}