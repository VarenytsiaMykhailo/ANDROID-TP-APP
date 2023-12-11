package com.example.app.presentationlayer.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.datalayer.models.NearbyPlace
import com.example.app.datalayer.models.PlaceReaction
import com.example.app.domain.providers.MapProvider
import io.paperdb.Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class VisitedPlacesViewModel : ViewModel() {

    private val mapProvider = MapProvider

    fun savePlace(place: NearbyPlace) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Paper.book(VISITED_PLACES).write(place.placeId, place)
                mapProvider.postSuggestReaction(place.placeId, PlaceReaction.Reaction.VISITED)
            }
        }

    fun removePlace(place: NearbyPlace) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Paper.book(VISITED_PLACES).delete(place.placeId)
                mapProvider.postSuggestReaction(place.placeId, PlaceReaction.Reaction.UNVISITED)
            }
        }

    companion object {

        private const val VISITED_PLACES = "visited_places"
    }
}