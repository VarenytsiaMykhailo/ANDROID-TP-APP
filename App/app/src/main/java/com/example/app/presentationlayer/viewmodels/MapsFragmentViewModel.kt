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
                fragment.onNewLocation(it.location.lat, it.location.lng, it.name)
            }
        }
    }
}