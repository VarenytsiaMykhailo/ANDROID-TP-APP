package com.example.app.businesslayer.providers

import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient

internal class MapAndroidClient {

    /**
     * Example: "ChIJfRJDflpKtUYRl0UbgcrmUUk", listOf(Place.Field.ID, Place.Field.NAME)
     */
    fun fetchPlace(
        placeId: String,
        placeFieldsToLoad: List<Place.Field>,
        onSuccess: (place: Place) -> Unit,
        onFail: () -> Unit,
    ) {
        // Construct a request object, passing the place ID and fields array.
        val request = FetchPlaceRequest.builder(placeId, placeFieldsToLoad)
            .build()

        // Add a listener to handle the response.
        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val place = response.place
            Log.i(LOG_TAG, "Place found: " + place.name)
            onSuccess(place)
        }.addOnFailureListener { exception ->
            if (exception is ApiException) {
                val statusCode = exception.statusCode
                // Handle error with given status code.
                Log.e(
                    LOG_TAG,
                    "Error while fetching place: ${exception.message}, statusCode = $statusCode"
                )
                onFail()
            }
        }
    }

    fun fetchPhoto(
        photoReference: String,
        //maxHeight: Int,
        //maxWidth: Int,
        onSuccess: (bitmap: Bitmap) -> Unit,
        onFail: () -> Unit,
    ) {
        val photoMetadata =
            PhotoMetadata.builder("AUacShh3_Dd8yvV2JZMtNjjbbSbFhSv-0VmUN-uasQ2Oj00XB63irPTks0-A_1rMNfdTunoOVZfVOExRRBNrupUf8TY4Kw5iQNQgf2rwcaM8hXNQg7KDyvMR5B-HzoCE1mwy2ba9yxvmtiJrdV-xBgO8c5iJL65BCd0slyI1")
                //.setHeight(maxHeight)
                //.setWidth(maxWidth)
                .build()

        // Create a FetchPhotoRequest.
        val photoRequest = FetchPhotoRequest.builder(photoMetadata)
            //.setMaxWidth(500) // Optional.
            //.setMaxHeight(300) // Optional.
            .build()
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
            val bitmap = fetchPhotoResponse.bitmap
            onSuccess(bitmap)
        }.addOnFailureListener { exception ->
            if (exception is ApiException) {
                val statusCode = exception.statusCode
                // Handle error with given status code.
                Log.e(
                    LOG_TAG,
                    "Error while fetching photo: ${exception.message}, statusCode = $statusCode"
                )
                onFail()
            }
        }
    }

    companion object {

        lateinit var placesClient: PlacesClient

        private const val LOG_TAG = "MapAndroidClient"
    }
}