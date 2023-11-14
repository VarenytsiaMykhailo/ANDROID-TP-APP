package com.example.app.presentationlayer.viewmodels

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.domain.providers.MapProvider
import com.example.app.datalayer.models.NearbyPlace
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

    /*
    fun onVisitedPlace(position: Int, placeToVisited: NearbyPlace): Int =
        removePlace(position, placeToVisited).also {
            postSuggestReaction(
                placeToVisited.placeId,
                PlaceReaction.Reaction.VISITED
            )
        }

    fun onRestoreVisitedPlace(
        favoritePlacesListPosition: Int,
        placesCachedListPosition: Int,
        placeToRestore: NearbyPlace,
    ) {
        restorePlace(favoritePlacesListPosition, placesCachedListPosition, placeToRestore)
        // TODO поменять на отмену VISITED, как появится
        /*
        postSuggestReaction(
            deletedLocation.placeId,
            PlaceReaction.Reaction.VISITED
        )
         */
    }

    private fun removePlace(favoritePlacesListPosition: Int, placeToVisited: NearbyPlace): Int {
        Log.d("qwerty123", "favoritePlacesList bd = $favoritePlacesList favoritePlacesListPosition = $favoritePlacesListPosition")
        favoritePlacesList.removeAt(favoritePlacesListPosition)
        Log.d("qwerty123", "favoritePlacesList ad = $favoritePlacesList")

        val indexOfCachedListForRestoring = mapProvider.placesCachedList.indexOfFirst {
            it.placeId == placeToVisited.placeId
        }
        mapProvider.placesCachedList.removeIf {
            it.placeId == placeToVisited.placeId
        }
        placesListRecyclerViewAdapter.notifyItemRemoved(favoritePlacesListPosition)

        return indexOfCachedListForRestoring
    }

    private fun restorePlace(
        favoritePlacesListPosition: Int,
        placesCachedListPosition: Int,
        placeToRestore: NearbyPlace,
    ) {
        favoritePlacesList.add(favoritePlacesListPosition, placeToRestore)
        mapProvider.placesCachedList.add(placesCachedListPosition, placeToRestore)
        placesListRecyclerViewAdapter.notifyItemInserted(favoritePlacesListPosition)
    }

    private fun postSuggestReaction(placeId: String, reaction: PlaceReaction.Reaction) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                mapProvider.postSuggestReaction(placeId, reaction)
            }
        }

     */

    companion object {

        private const val FAVORITE_PLACES = "favorite_places"
    }
}