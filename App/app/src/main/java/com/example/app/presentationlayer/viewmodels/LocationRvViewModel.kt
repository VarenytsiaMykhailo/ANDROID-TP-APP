package com.example.app.presentationlayer.viewmodels

import androidx.lifecycle.ViewModel
import com.example.app.businesslayer.providers.LocationProvider
import com.example.app.datalayer.accessesors.LocationsAccessor

class LocationRvViewModel : ViewModel() {
    private val accessor = LocationsAccessor.create()
    private val provider = LocationProvider(accessor)

    suspend fun getFriends() = provider.getLocations(10)

    suspend fun getFriend(id: String) = provider.getLocation(id)
}