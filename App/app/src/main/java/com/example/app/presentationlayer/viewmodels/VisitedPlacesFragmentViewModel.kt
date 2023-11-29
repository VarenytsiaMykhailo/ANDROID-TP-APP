package com.example.app.presentationlayer.viewmodels

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.datalayer.models.NearbyPlace
import com.example.app.domain.providers.MapProvider
import com.example.app.presentationlayer.adapters.PlacesListRecyclerViewAdapter
import com.example.app.presentationlayer.fragments.visitedplaceslistscreen.VisitedPlacesListFragment
import io.paperdb.Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class VisitedPlacesFragmentViewModel : ViewModel() {

    private val mapProvider = MapProvider

    lateinit var fragment: VisitedPlacesListFragment

    lateinit var placesListRecyclerViewAdapter: PlacesListRecyclerViewAdapter

    lateinit var visitedPlacesList: MutableList<NearbyPlace>

    fun onUpdatePlaces() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                visitedPlacesList = getVisitedPlaces()
                Handler(Looper.getMainLooper()).post {
                    placesListRecyclerViewAdapter.submitList(visitedPlacesList)
                }
            }
        }
    }

    private fun getVisitedPlaces(): MutableList<NearbyPlace> =
        mutableListOf<NearbyPlace>().apply {
            Paper.book(VISITED_PLACES).allKeys.forEach {
                Paper.book(VISITED_PLACES).read<NearbyPlace>(it)?.run {
                    add(this)
                }
            }
        }

    companion object {

        private const val VISITED_PLACES = "visited_places"
    }
}