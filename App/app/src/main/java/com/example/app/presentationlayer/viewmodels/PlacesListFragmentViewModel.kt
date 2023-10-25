package com.example.app.presentationlayer.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.businesslayer.providers.MapProvider
import com.example.app.presentationlayer.adapters.PlacesListRecyclerViewAdapter
import kotlinx.coroutines.launch

internal class PlacesListFragmentViewModel : ViewModel() {

    private val mapProvider = MapProvider()

    lateinit var placesListRecyclerViewAdapter: PlacesListRecyclerViewAdapter

    fun onUpdatePlaces() {
        viewModelScope.launch {
            val placesList = mapProvider.getPlaces()
            placesListRecyclerViewAdapter.submitList(placesList)
        }
    }
}