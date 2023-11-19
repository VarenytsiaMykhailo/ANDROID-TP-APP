package com.example.app.domain.providers

import com.example.app.datalayer.models.NearbyPlace
import com.example.app.datalayer.models.PlaceDescription

fun PlaceDescription.toNearbyPlace() =
    NearbyPlace(
        this.placeId,
        this.name,
        this.photos[0],
        this.rating,
        this.ratingCount,
        this.reactions,
        NearbyPlace.Location(
            this.location.lat,
            this.location.lng,
        )
    )