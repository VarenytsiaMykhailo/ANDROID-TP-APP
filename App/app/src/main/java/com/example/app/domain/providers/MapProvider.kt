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
        placesTypes: String? = null,
    ): List<NearbyPlace> {
        // Log.d("sss","$updateListByRadiusFlag $forceRefresh")
        if (updateListByRadiusFlag || forceRefresh) {
            placesCachedList = mapRepository.getSuggestPlaces(
                "$lat,$lng",
                radius.toString(),
                limit,
                offset,
                placesTypes,
            ).also {
                Log.d(LOG_TAG, "getSuggestPlaces = $it")
            }.toMutableList()

            updateListByRadiusFlag = false
        }

        return placesCachedList
    }

    suspend fun getSuggestCategoriesList(): CategoriesList =
        mapRepository.getSuggestCategoriesList().also {
            Log.d(LOG_TAG, "getSuggestCategoriesList = $it")
        }

    /**
     * @param placeId Example: "ChIJfRJDflpKtUYRl0UbgcrmUUk".
     */
    suspend fun getPlaceDescription(placeId: String): PlaceDescription =
        mapRepository.getPlaceDescription(placeId).also {
            Log.d(LOG_TAG, "getPlaceDescription = $it")
        }

    suspend fun postSuggestReaction(placeId: String, reaction: PlaceReaction.Reaction) =
        mapRepository.postSuggestReaction(
            PlaceReaction(placeId, reaction)
        )

    suspend fun postSuggestUserNew() =
        mapRepository.postSuggestUserNew()

    suspend fun postSuggestRoute(routeRequest: RouteRequest): RouteResponse =
        mapRepository.postSuggestRoute(
            routeRequest
        )

    suspend fun postSuggestRouteSortPlace(sortPlacesRequest: SortPlacesRequest): SortPlaceResponse =
        mapRepository.postSuggestRouteSortPlace(
            sortPlacesRequest
        )

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