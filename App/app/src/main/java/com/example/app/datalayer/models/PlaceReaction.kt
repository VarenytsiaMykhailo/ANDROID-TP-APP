package com.example.app.datalayer.models

import com.google.gson.annotations.SerializedName

data class PlaceReaction (

    @SerializedName("place_id")
    val placeId: String,

    @SerializedName("reaction")
    val reaction: Reaction
) {

    enum class Reaction {
        @SerializedName("like")
        LIKE,
        @SerializedName("visited")
        VISITED,
        @SerializedName("refuse")
        REFUSE,
        @SerializedName("unlike")
        UNLIKE,
        @SerializedName("unvisited")
        UNVISITED,
        @SerializedName("unrefuse")
        UNREFUSE,
    }
}

