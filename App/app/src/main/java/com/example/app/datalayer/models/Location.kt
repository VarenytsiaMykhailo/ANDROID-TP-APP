package com.example.app.datalayer.models

import com.google.gson.annotations.SerializedName

class Location {
    // в @ лежит название переменной на бэке

    @SerializedName("id")
    var id = ""
    fun id() = id

    @SerializedName("place_id")
    var place_id = ""
    fun place_id() = place_id

    @SerializedName("name")
    var name = ""
    fun name() = name

    @SerializedName("cover")
    var image_url = ""
    fun imageUrl() = image_url

    @SerializedName("rating")
    var rating :Float= 0F
    fun rating() = rating

    @SerializedName("rating_count")
    var rating_count:Int=0
    fun rating_count() = rating_count

    @SerializedName("lat")
    var lat=""
    fun lat() = lat

    @SerializedName("lng")
    var lng=""
    fun lng() = lng


}