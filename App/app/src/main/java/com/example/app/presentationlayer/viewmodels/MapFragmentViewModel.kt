package com.example.app.presentationlayer.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.domain.providers.MapProvider
import com.example.app.datalayer.models.RouteRequest
import com.example.app.datalayer.models.SortPlacesRequest
import com.example.app.presentationlayer.fragments.mapscreen.MapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.launch

internal class MapFragmentViewModel : ViewModel() {

    private val mapProvider = MapProvider

    lateinit var fragment: MapFragment

    fun onUpdatePlaces(
        shouldUpdateCachedValue: Boolean
    ) {

        fragment.refreshMap()

        if (shouldUpdateCachedValue) {
            viewModelScope.launch {
                val placesList = mapProvider.getSuggestPlaces(
                    fragment.mainActivity.usersLastChosenLocation.latitude,
                    fragment.mainActivity.usersLastChosenLocation.longitude,
                    20,
                    0,
                    forceRefresh = true,
                )
                placesList.forEach {
                    fragment.addAdvancedMarker(it.location.lat, it.location.lng, it.name)
                }
            }
        } else {
            mapProvider.placesCachedList.forEach {
                fragment.addAdvancedMarker(it.location.lat, it.location.lng, it.name)
            }
        }
    }

    fun onGoogleMapRoute(start: LatLng, end: LatLng, waypoints: List<LatLng>) {
        viewModelScope.launch {
            val waypointsRequest = mutableListOf<SortPlacesRequest.Location>()
            waypoints.forEach {
                waypointsRequest += SortPlacesRequest.Location(
                    it.latitude,
                    it.longitude
                )
            }

            val sortPlacesRequest = SortPlacesRequest(
                start = SortPlacesRequest.Location(start.latitude, start.longitude),
                end = SortPlacesRequest.Location(end.latitude, end.longitude),
                waypoints = waypointsRequest
            )
            val sortPlaceResponse = mapProvider.postSuggestRouteSortPlace(sortPlacesRequest)

            var requestUrl = "https://www.google.com/maps/dir/?api=1"
            requestUrl += "&origin=${sortPlacesRequest.start.lat},${sortPlacesRequest.start.lng}"
            requestUrl += "&destination=${sortPlacesRequest.end.lat},${sortPlacesRequest.end.lng}"

            if (sortPlaceResponse.waypoints.size >= 1) {
                requestUrl += "&waypoints="
            }
            sortPlaceResponse.waypoints.forEachIndexed { index, location ->
                requestUrl += "${location.lat},${location.lng}"
                if (index != sortPlaceResponse.waypoints.size - 1) {
                    requestUrl += "|"
                }
            }
            requestUrl += "&travelmode=walk"

            fragment.launchGoogleMapApp(requestUrl)
        }
    }

    fun onDrawRoute(start: LatLng, end: LatLng, waypoints: List<LatLng>) {
        viewModelScope.launch {
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

            val encodedPolylines = mutableListOf<String>()
            routeResponseList.route.forEach {
                encodedPolylines += it.polyline
            }

            val pointsList = mutableListOf<LatLng>()
            encodedPolylines.forEach {
                val pointsListOfPolyline = PolyUtil.decode(it)
                pointsList.addAll(pointsListOfPolyline)
            }

            fragment.onDrawRoute(pointsList)
            fragment.addCenterRouteMarker(start.latitude, start.longitude)
        }
    }

    fun getPlaceIdByLatLng(latLng: LatLng): String? =
        mapProvider.placesCachedList.firstOrNull() {
            it.location.lat == latLng.latitude && it.location.lng == latLng.longitude
        }?.placeId

    // TODO придумать способ как улучшить
    fun increaseRadius() {
        MapProvider.increaseRadius()
        onUpdatePlaces(shouldUpdateCachedValue = true)
    }

    fun decreaseRadius(): Boolean {
        val res = MapProvider.decreaseRadius()
        onUpdatePlaces(shouldUpdateCachedValue = true)
        return res
    }

    fun giveRadiusString() = ((mapProvider.radius).toDouble() / 1000).toString()
}