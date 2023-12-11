package com.example.app.presentationlayer.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.datalayer.models.CategoriesList
import com.example.app.domain.providers.MapProvider
import com.example.app.datalayer.models.NearbyPlace
import com.example.app.datalayer.models.PlaceReaction
import com.example.app.presentationlayer.adapters.FiltersAdapter
import com.example.app.presentationlayer.adapters.PlacesListRecyclerViewAdapter
import com.example.app.presentationlayer.fragments.placeslistscreen.PlacesListFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class PlacesListFragmentViewModel : ViewModel() {

    private val mapProvider = MapProvider

    lateinit var fragment: PlacesListFragment

    lateinit var placesListRecyclerViewAdapter: PlacesListRecyclerViewAdapter

    lateinit var filtersAdapter: FiltersAdapter

    lateinit var filters: CategoriesList

    lateinit var placesList: MutableList<NearbyPlace>

    fun onUpdatePlaces(
        forceRefresh: Boolean = false, // Need for ignoring isDataAlreadyLoaded flag,
        placesTypes: String? = null,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                placesList =
                    mapProvider.getSuggestPlaces(
                        fragment.mainActivity.usersLastChosenLocation.latitude,
                        fragment.mainActivity.usersLastChosenLocation.longitude,
                        20,
                        0,
                        forceRefresh,
                    ).toMutableList()
            }
            fragment.mainActivity.usersPreviousChosenLocation =
                fragment.mainActivity.usersLastChosenLocation
            placesListRecyclerViewAdapter.submitList(placesList)
        }
    }

    fun onGetFilters() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                filters=mapProvider.getSuggestCategoriesList()
            }
            val filtersList:MutableList<String> = filters.categories.toMutableList()
            filtersList.add(0,"Избранное")
            filtersList.add(0,"Не посещенное")
            filtersAdapter.submitList(filtersList)
        }
    }

    fun onAddFilter(name: String) {
        mapProvider.filtersList[name] = name
    }

    fun onRemoveFilter(name: String) {
        mapProvider.filtersList.remove(name, name)
    }

    fun onFilterChosen(name:String)=mapProvider.filtersList.containsKey(name)

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