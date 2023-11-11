package com.example.app.presentationlayer.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.domain.providers.MapProvider
import com.example.app.datalayer.models.NearbyPlace
import com.example.app.datalayer.models.PlaceReaction
import com.example.app.presentationlayer.adapters.PlacesListRecyclerViewAdapter
import com.example.app.presentationlayer.fragments.placeslistscreen.PlacesListFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class PlacesListFragmentViewModel : ViewModel() {

    private val mapProvider = MapProvider

    lateinit var fragment: PlacesListFragment

    lateinit var placesListRecyclerViewAdapter: PlacesListRecyclerViewAdapter

    private var isDataAlreadyLoaded = false // TODO Костыль, подумать как сделать лучше

    lateinit var placesList: MutableList<NearbyPlace>

    // A default location to use when location permission is not granted. Moscow, Red Square.
    private val defaultLocation = LatLng(55.753544, 37.621202)

    fun onUpdatePlaces(
        forceRefresh: Boolean = false, // Need for ignoring isDataAlreadyLoaded flag
    ) {
        if (!isDataAlreadyLoaded || forceRefresh) {
            fragment.mainActivity.updateDeviceLocation(
                onSuccess = {
                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            placesList =
                                mapProvider.getSuggestPlaces(
                                    it.latitude,
                                    it.longitude,
                                    20,
                                    0,
                                    forceRefresh
                                ).toMutableList()
                        }
                        placesListRecyclerViewAdapter.submitList(placesList)
                        isDataAlreadyLoaded = true
                    }
                },
                onFail = {
                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            placesList =
                                mapProvider.getSuggestPlaces(
                                    defaultLocation.latitude,
                                    defaultLocation.longitude,
                                    20,
                                    0,
                                    forceRefresh
                                ).toMutableList()
                        }
                        placesListRecyclerViewAdapter.submitList(placesList)
                        isDataAlreadyLoaded = true
                    }
                }
            )
        }
    }

    fun onDeletePlace(position: Int) =
        mapProvider.placesCachedList.removeAt(position)

    fun onRestorePlace(position: Int, restoredLocation: NearbyPlace) =
        mapProvider.placesCachedList.add(position, restoredLocation)

    fun postSuggestReaction(placeId: String, reaction: PlaceReaction.Reaction) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) { mapProvider.postSuggestReaction(placeId, reaction) }
        }
}