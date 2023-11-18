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
        } else {
            placesListRecyclerViewAdapter.submitList(placesList)
        }
    }

    fun onRemovePlace(position: Int, placeToDelete: NearbyPlace) {
        removePlace(position)
        postSuggestReaction(
            placeToDelete.placeId,
            PlaceReaction.Reaction.REFUSE
        )
    }

    fun onVisitedPlace(position: Int, placeToVisited: NearbyPlace) {
        removePlace(position)
        postSuggestReaction(
            placeToVisited.placeId,
            PlaceReaction.Reaction.VISITED
        )
    }

    fun onRestoreRemovedPlace(position: Int, placeToRestore: NearbyPlace) {
        restorePlace(position, placeToRestore)

        postSuggestReaction(
            placeToRestore.placeId,
            PlaceReaction.Reaction.UNREFUSE
        )

    }

    fun onRestoreVisitedPlace(position: Int, placeToRestore: NearbyPlace) {
        restorePlace(position, placeToRestore)

        postSuggestReaction(
            placeToRestore.placeId,
            PlaceReaction.Reaction.UNVISITED
        )

    }

    private fun removePlace(position: Int) {
        placesList.removeAt(position)
        mapProvider.placesCachedList.removeAt(position)
        placesListRecyclerViewAdapter.notifyItemRemoved(position)
    }

    private fun restorePlace(position: Int, placeToRestore: NearbyPlace) {
        placesList.add(position, placeToRestore)
        mapProvider.placesCachedList.add(position, placeToRestore)
        placesListRecyclerViewAdapter.notifyItemInserted(position)
    }

    private fun postSuggestReaction(placeId: String, reaction: PlaceReaction.Reaction) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                mapProvider.postSuggestReaction(placeId, reaction)
            }
        }
}