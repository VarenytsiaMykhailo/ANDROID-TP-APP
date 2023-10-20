package com.example.app.businesslayer.providers

import com.example.app.datalayer.accessesors.LocationsAccessor
import com.example.app.datalayer.models.Location

class LocationProvider(private val accessor: LocationsAccessor) {

    suspend fun getLocations(): List<Location> {
        return accessor.getLocations()
    }

    suspend fun getLocation(id: String): List<Location> {
        return accessor.getLocation(id)
    }
}