package com.example.app.presentationlayer.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.domain.providers.MapProvider
import com.example.app.datalayer.models.NearbyPlace
import com.example.app.datalayer.models.PlaceReaction
import com.example.app.presentationlayer.adapters.PlacesListRecyclerViewAdapter
import com.example.app.presentationlayer.fragments.LikedPlacesFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class LikedPlacesFragmentViewModel : ViewModel() {

    private val mapProvider = MapProvider

    lateinit var fragment: LikedPlacesFragment

    lateinit var placesListRecyclerViewAdapter: PlacesListRecyclerViewAdapter

    lateinit var placesList: MutableList<NearbyPlace>


    fun onUpdatePlaces() {
        try {placesListRecyclerViewAdapter.submitList(placesList)}
        catch (_: UninitializedPropertyAccessException){ }
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