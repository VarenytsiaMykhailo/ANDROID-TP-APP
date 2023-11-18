package com.example.app.domain.providers
import android.util.Log
import com.example.app.datalayer.models.NearbyPlace
import com.example.app.datalayer.models.PlaceDescription

fun placeClassesTransformer(place: PlaceDescription): NearbyPlace {
    return NearbyPlace(
        place.placeId,
        place.name,
        place.photos[0],
        place.rating,
        place.ratingCount,
        place.reactions,
        place.location
    )
}