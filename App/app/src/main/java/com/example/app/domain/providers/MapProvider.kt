package com.example.app.domain.providers

import android.util.Log
import com.example.app.datalayer.models.CategoriesList
import com.example.app.datalayer.models.NearbyPlace
import com.example.app.datalayer.models.PlaceDescription
import com.example.app.datalayer.models.PlaceReaction
import com.example.app.datalayer.models.RouteRequest
import com.example.app.datalayer.models.RouteResponse
import com.example.app.datalayer.models.SortPlacesRequest
import com.example.app.datalayer.models.SortPlaceResponse
import com.example.app.datalayer.repositories.MapRepository

object MapProvider {

    private const val LOG_TAG = "MapProvider"

    private val mapRepository = MapRepository.mapRepository

    private var updateListByRadiusFlag = true

    var radius = 3000

    lateinit var placesCachedList: MutableList<NearbyPlace>
    var filtersList: MutableMap<String, String> = mutableMapOf()

    // TODO чет не нравится что из параметра убрали radius. Придумать как отрефакторить
    /**
     * @param lat Example: 55.753544.
     * @param lng Example: 37.621202.
     * @param radius Example: 50000 in metres.
     * @param limit Example: 10.
     * @param offset Example: 10.
     */
    suspend fun getSuggestPlaces(
        lat: Double,
        lng: Double,
        limit: Int,
        offset: Int,
        forceRefresh: Boolean = false, // Need for ignoring updateListFlag,
    ): List<NearbyPlace> {
        stringFromMap(filtersList)?.let { Log.d("sss", it) }
        if (updateListByRadiusFlag || forceRefresh) {
            placesCachedList =
                try {
                    mapRepository.getSuggestPlaces(
                        "$lat,$lng",
                        radius.toString(),
                        limit,
                        offset,
                        stringFromMap(filtersList),
                    ).also {
                        Log.d(LOG_TAG, "getSuggestPlaces = $it")
                    }.toMutableList()
                } catch (e: Exception) {
                    Log.e(LOG_TAG, e.message, e)
                    emptyList<NearbyPlace>().toMutableList()
                }

            updateListByRadiusFlag = false
        }
        return placesCachedList
    }

    suspend fun getSuggestCategoriesList(): CategoriesList =
        try {
            mapRepository.getSuggestCategoriesList().also {
                Log.d(LOG_TAG, "getSuggestCategoriesList = $it")
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message, e)
            CategoriesList(emptyList())
        }

    /**
     * @param placeId Example: "ChIJfRJDflpKtUYRl0UbgcrmUUk".
     */
    suspend fun getPlaceDescription(placeId: String): PlaceDescription =
        try {
            mapRepository.getPlaceDescription(placeId).also {
                Log.d(LOG_TAG, "getPlaceDescription = $it")
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message, e)
            PlaceDescription(
                placeId = "",
                _name = "",
                _rating = 0.0,
                _ratingCount = 0,
                _description = "",
                _address = "",
                _workingHours = emptyList(),
                _tags = emptyList(),
                _photos = null,
                location = PlaceDescription.Location(0.0, 0.0),
                _reactions = emptyList()
            )
        }

    suspend fun postSuggestReaction(placeId: String, reaction: PlaceReaction.Reaction) =
        try {
            mapRepository.postSuggestReaction(
                PlaceReaction(placeId, reaction)
            )
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message, e)
        }

    suspend fun postSuggestUserNew() =
        mapRepository.postSuggestUserNew()

    suspend fun postSuggestRoute(routeRequest: RouteRequest): RouteResponse =
        try {
            mapRepository.postSuggestRoute(
                routeRequest
            )
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message, e)
            RouteResponse(
                travelMode = "WALK",
                route = emptyList(),
            )
        }


    suspend fun postSuggestRouteSortPlace(sortPlacesRequest: SortPlacesRequest): SortPlaceResponse =
        try {
            mapRepository.postSuggestRouteSortPlace(
                sortPlacesRequest
            )
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message, e)
            SortPlaceResponse(
                start = SortPlaceResponse.Location(
                    sortPlacesRequest.start.lat,
                    sortPlacesRequest.start.lng,
                ),
                end = SortPlaceResponse.Location(
                    sortPlacesRequest.end.lat,
                    sortPlacesRequest.end.lng,
                ),
                waypoints = mutableListOf<SortPlaceResponse.Location>().apply {
                    sortPlacesRequest.waypoints.forEach {
                        this += SortPlaceResponse.Location(
                            it.lat,
                            it.lng,
                        )
                    }
                },
            )
        }

    suspend fun getPing(): Boolean =
        try {
            mapRepository.getPing()
            true
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message, e)
            false
        }

    private fun stringFromMap(map: MutableMap<String, String>): String? {
        var string: String? = null
        map.values.forEach {
            if (string == null) string = ""
            string = "$string$it,"
        }
        string = string?.dropLast(1)
        return string
    }

    fun increaseRadius() {
        radius += 500
        updateListByRadiusFlag = true
    }

    fun decreaseRadius(): Boolean {
        return if (radius != 500) {
            radius -= 500
            false
        } else {
            updateListByRadiusFlag = true
            true
        }
    }
}