package com.example.app.presentationlayer.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.domain.providers.MapProvider
import com.example.app.datalayer.models.NearbyPlace
import com.example.app.datalayer.models.RouteRequest
import com.example.app.presentationlayer.fragments.mapscreen.MapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.launch

internal class MapFragmentViewModel : ViewModel() {

    private val mapProvider = MapProvider

    lateinit var fragment: MapFragment

    lateinit var placesList: MutableList<NearbyPlace>

    // A default location to use when location permission is not granted. Moscow, Red Square.
    private val defaultLocation = LatLng(55.753544, 37.621202)

    fun onUpdatePlaces(shouldUseCachedValue: Boolean = true) {
        if (shouldUseCachedValue) {
            mapProvider.placesCachedList.forEach {
                fragment.onNewLocation(it.location.lat, it.location.lng, it.name)
            }
        } else {
            viewModelScope.launch {
                val placesList = mapProvider.getSuggestPlaces(
                    mapProvider.lastUsedLat,
                    mapProvider.lastUsedLng,
                    20,
                    0
                )
                placesList.forEach {
                    fragment.onNewLocation(it.location.lat, it.location.lng, it.name)
                }
            }

        }
    }

    fun onDrawRoute() {
        val encodedPolylines = listOf("uam~FtfbvOlhEayA}vBwIpp@oaA")
        val pointsList = mutableListOf<LatLng>()

        mapProvider.placesCachedList
        /*
        mapProvider.postSuggestRoute(RouteRequest(
            travelMode = RouteRequest.TravelMode.WALK,


        ))

         */

        encodedPolylines.forEach {
            val pointsListOfPolyline = PolyUtil.decode(it)
            Log.d("qwerty123", "polyline = $it pointsListOfPolyline = $pointsListOfPolyline")
            pointsList.addAll(pointsListOfPolyline)
        }

        Log.d("qwerty123", "pointsList = $pointsList")

        fragment.onDrawRoute(pointsList)
    }

    // TODO придумать способ как улучшить
    fun updateRadius(newRadius: String) {
        mapProvider.updateRadius((newRadius.toDouble() * 1000).toInt())
        onUpdatePlaces(shouldUseCachedValue = false)
    }

    fun giveRadiusString() = ((mapProvider.radius).toDouble() / 1000).toString()
}