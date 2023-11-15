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

    fun onUpdatePlaces(
        shouldUseCachedValue: Boolean = true,
        shouldRefreshMapBefore: Boolean = true,
    ) {
        fragment.refreshMap()
        if (shouldUseCachedValue) {
            mapProvider.placesCachedList.forEach {
                fragment.addAdvancedMarker(it.location.lat, it.location.lng, it.name)
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
                    fragment.addAdvancedMarker(it.location.lat, it.location.lng, it.name)
                }
            }
        }
    }

    fun onDrawRoute(start: LatLng, end: LatLng, waypoints: List<LatLng>) {
        viewModelScope.launch {
            Log.d("qwerty123", "onDrawRoute - enter")

            //Log.d("qwerty123", "response route list = $list")

            val waypointsRequest = mutableListOf<RouteRequest.Waypoint>()
            waypoints.forEach {
                waypointsRequest += RouteRequest.Waypoint(
                    "",
                    RouteRequest.Location(
                        it.latitude,
                        it.longitude,
                    )
                )
            }

            Log.d("qwerty123", "waypointsRequest = $waypointsRequest")

            val routeResponseList = mapProvider.postSuggestRoute(
                RouteRequest(
                    travelMode = RouteRequest.TravelMode.WALK,
                    start = RouteRequest.Location(
                        start.latitude,
                        start.longitude,
                    ),
                    end = RouteRequest.Location(
                        end.latitude,
                        end.longitude,
                    ),
                    waypoints = waypointsRequest
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
            Log.d("qwerty123", "last = ${pointsList.last()}")

            fragment.onDrawRoute(pointsList)
        }
    }

    fun onDrawRouteOld(start: LatLng, end: LatLng, waypoints: List<LatLng>) {
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

    fun getPlaceIdByLatLng(latLng: LatLng): String? =
        mapProvider.placesCachedList.firstOrNull() {
            it.location.lat == latLng.latitude && it.location.lng == latLng.longitude
        }?.placeId

    // TODO придумать способ как улучшить
    fun updateRadius(newRadius: String) {
        mapProvider.updateRadius((newRadius.toDouble() * 1000).toInt())
        onUpdatePlaces(shouldUseCachedValue = false)
    }

    fun giveRadiusString() = ((mapProvider.radius).toDouble() / 1000).toString()
}