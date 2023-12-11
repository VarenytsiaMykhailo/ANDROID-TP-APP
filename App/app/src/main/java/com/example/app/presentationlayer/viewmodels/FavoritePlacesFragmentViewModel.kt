package com.example.app.presentationlayer.viewmodels

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.domain.providers.MapProvider
import com.example.app.datalayer.models.NearbyPlace
import com.example.app.datalayer.models.PlaceReaction
import com.example.app.presentationlayer.adapters.PlacesListRecyclerViewAdapter
import com.example.app.presentationlayer.fragments.favoriteplaceslistscreen.FavoritePlacesListFragment
import io.paperdb.Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class FavoritePlacesFragmentViewModel : ViewModel() {

    private val mapProvider = MapProvider

    lateinit var fragment: FavoritePlacesListFragment

    lateinit var placesListRecyclerViewAdapter: PlacesListRecyclerViewAdapter

    lateinit var favoritePlacesList: MutableList<NearbyPlace>

    fun onUpdatePlaces() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                favoritePlacesList = getFavoritePlaces()
                Handler(Looper.getMainLooper()).post {
                    placesListRecyclerViewAdapter.submitList(favoritePlacesList)
                }
            }
        }
    }

    private fun getFavoritePlaces(): MutableList<NearbyPlace> =
        mutableListOf<NearbyPlace>().apply {
            Paper.book(FAVORITE_PLACES).allKeys.forEach {
                Paper.book(FAVORITE_PLACES).read<NearbyPlace>(it)?.run {
                    add(this)
                }
            }
        }



 fun postSuggestReaction(placeId: String, reaction: PlaceReaction.Reaction) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                mapProvider.postSuggestReaction(placeId, reaction)
            }
        }



    companion object {

        private const val FAVORITE_PLACES = "favorite_places"
    }
}