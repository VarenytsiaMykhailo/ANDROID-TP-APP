package com.example.app.presentationlayer.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.datalayer.models.NearbyPlace
import com.example.app.datalayer.models.PlaceReaction
import com.example.app.domain.providers.MapProvider
import com.example.app.datalayer.models.RouteRequest
import com.example.app.datalayer.models.SortPlacesRequest
import com.example.app.presentationlayer.adapters.PlaceDescriptionImagesSliderRecyclerViewAdapter
import com.example.app.presentationlayer.fragments.mapscreen.MapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        fragment.addCenterRouteMarker()
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
        }
    }

    fun updateImageSlider(
        placeId: String,
        placeDescriptionImagesSliderRecyclerViewAdapter: PlaceDescriptionImagesSliderRecyclerViewAdapter,
    ) {
        viewModelScope.launch {
            val placeDescription = mapProvider.getPlaceDescription(placeId)
            placeDescriptionImagesSliderRecyclerViewAdapter.submitList(placeDescription.photos)
        }
    }

    fun getPlaceByLatLng(latLng: LatLng): NearbyPlace? =
        mapProvider.placesCachedList.firstOrNull() {
            it.location.lat == latLng.latitude && it.location.lng == latLng.longitude
        }

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

    fun onRemovePlace(placeToDelete: NearbyPlace) {
        removePlace(placeToDelete)
        postSuggestReaction(
            placeToDelete.placeId,
            PlaceReaction.Reaction.REFUSE
        )
    }

    fun onRestoreRemovedPlace(placeToRestore: NearbyPlace) {
        restorePlace(placeToRestore)

        postSuggestReaction(
            placeToRestore.placeId,
            PlaceReaction.Reaction.UNREFUSE
        )
    }

    private fun removePlace(placeToDelete: NearbyPlace) {
        //placesList.removeAt(position)
        mapProvider.placesCachedList.remove(placeToDelete)
        onUpdatePlaces(false)
    }

    private fun restorePlace(placeToRestore: NearbyPlace) {
        //placesList.add(position, placeToRestore)
        mapProvider.placesCachedList.add(placeToRestore)
        onUpdatePlaces(false)
    }

    private fun postSuggestReaction(placeId: String, reaction: PlaceReaction.Reaction) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                mapProvider.postSuggestReaction(placeId, reaction)
            }
        }
}