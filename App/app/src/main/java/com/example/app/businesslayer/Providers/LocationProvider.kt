package com.example.app.businesslayer.Providers

import com.example.app.datalayer.Accessesors.LocationsAccessor
import com.example.app.datalayer.Models.Location

class LocationProvider(private val accessor: LocationsAccessor) {

    suspend fun getLocations( limit: Int): List<Location> {
        return accessor.getLocations( limit)
    }

    suspend fun getLocation(id: String): List<Location> {
        return accessor.getLocation(id)
    }
}