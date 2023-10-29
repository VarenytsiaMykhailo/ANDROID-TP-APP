package com.example.app.presentationlayer.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.businesslayer.providers.MapProvider
import com.example.app.datalayer.models.Place
import com.example.app.presentationlayer.adapters.PlacesListRecyclerViewAdapter
import kotlinx.coroutines.launch

internal class PlacesListFragmentViewModel : ViewModel() {

    private val mapProvider = MapProvider()

    lateinit var placesListRecyclerViewAdapter: PlacesListRecyclerViewAdapter

    lateinit var placesList:MutableList<Place>

    fun onUpdatePlaces() {
        viewModelScope.launch {
            placesList = mapProvider.getPlaces().toMutableList()
            placesListRecyclerViewAdapter.submitList(placesList)
        }
    }
}