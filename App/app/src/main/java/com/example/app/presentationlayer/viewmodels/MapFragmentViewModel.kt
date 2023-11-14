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
        viewModelScope.launch {
            Log.d("qwerty123", "onDrawRoute - enter")


            val list = mapProvider.placesCachedList.take(5)

            //Log.d("qwerty123", "response route list = $list")

            val start = list.first()
            val end = list.last()

            val waypoints = mutableListOf<RouteRequest.Waypoint>()
            list.forEachIndexed { index, nearbyPlace ->
                if (index != 0 && index != 4) {
                    val waypoint = RouteRequest.Waypoint(
                        nearbyPlace.placeId,
                        RouteRequest.Location(
                            nearbyPlace.location.lat,
                            nearbyPlace.location.lng,
                        )
                    )

                    waypoints.add(waypoint)
                }
            }

            val routeResponseList = mapProvider.postSuggestRoute(
                RouteRequest(
                    travelMode = RouteRequest.TravelMode.WALK,
                    start = RouteRequest.Location(
                        start.location.lat,
                        start.location.lng,
                    ),
                    end = RouteRequest.Location(
                        end.location.lat,
                        end.location.lng,
                    ),
                    waypoints = waypoints
                )
            )
            Log.d("qwerty123", "response routeResponseList = $routeResponseList")

            val encodedPolylines = mutableListOf<String>()
            routeResponseList.route.forEach {
                encodedPolylines += it.polyline
            }

            Log.d("qwerty123", "response encodedPolylines = $encodedPolylines")




            //val encodedPolylines = listOf("uam~FtfbvOlhEayA}vBwIpp@oaA")
            val pointsList = mutableListOf<LatLng>()
            encodedPolylines.forEach {
                val pointsListOfPolyline = PolyUtil.decode(it)
                Log.d("qwerty123", "polyline = $it pointsListOfPolyline = $pointsListOfPolyline")
                pointsList.addAll(pointsListOfPolyline)
            }

            Log.d("qwerty123", "pointsList = $pointsList")

            fragment.onDrawRoute(pointsList)
        }
    }

    // TODO придумать способ как улучшить
    fun increaseRadius() {
        MapProvider.increaseRadius()
        onUpdatePlaces(shouldUseCachedValue = false)}

    fun decreaseRadius():Boolean {
        onUpdatePlaces(shouldUseCachedValue = false)
        return MapProvider.decreaseRadius()
    }

    fun giveRadiusString() = ((mapProvider.radius).toDouble() / 1000).toString()
}