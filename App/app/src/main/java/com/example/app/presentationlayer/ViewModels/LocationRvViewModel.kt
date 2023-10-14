package com.example.app.presentationlayer.ViewModels

import androidx.lifecycle.ViewModel
import com.example.app.businesslayer.Providers.LocationProvider
import com.example.app.datalayer.Accessesors.LocationsAccessor

class LocationRvViewModel : ViewModel() {
    private val accessor = LocationsAccessor.create()
    private val provider = LocationProvider(accessor)

    suspend fun getFriends() = provider.getLocations(10)

    suspend fun getFriend(id: String) = provider.getLocation(id)
}