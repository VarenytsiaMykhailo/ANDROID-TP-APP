package com.example.app.presentationlayer.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.datalayer.models.NearbyPlace
import io.paperdb.Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class FavoritePlacesViewModel : ViewModel() {

    fun savePlace(place: NearbyPlace) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Paper.book(FAVORITE_PLACES).write(place.placeId, place)
            }
        }

    fun removePlace(place: NearbyPlace) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Paper.book(FAVORITE_PLACES).delete(place.placeId)
            }
        }

    // TODO завернуть в корутину https://stackoverflow.com/questions/60910978/how-to-return-value-from-async-coroutine-scope-such-as-viewmodelscope-to-your-ui
    fun placeExists(place: NearbyPlace) =
        Paper.book(FAVORITE_PLACES).read<NearbyPlace>(place.placeId) != null

    companion object {

        private const val FAVORITE_PLACES = "favorite_places"
    }
}