package com.example.app.presentationlayer.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.businesslayer.providers.MapProvider
import com.example.app.presentationlayer.fragments.MapsFragment
import kotlinx.coroutines.launch

internal class MapsFragmentViewModel : ViewModel() {

    private val mapProvider = MapProvider()

    lateinit var fragment: MapsFragment

    fun onUpdatePlaces() {
        viewModelScope.launch {
            val placesList = mapProvider.getPlaces()

            placesList.forEach {
                val lat = it.location.lat
                val lng = it.location.lng
                val name = it.name

                fragment.onNewLocation(lat, lng, name)
            }
        }
    }
}