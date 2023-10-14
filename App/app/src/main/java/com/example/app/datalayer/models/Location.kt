package com.example.app.datalayer.models

import com.google.gson.annotations.SerializedName

class Location {
    // в @ лежит название переменной на бэке

    @SerializedName("image_url")
    var image_url = ""
    fun imageUrl() = image_url

    @SerializedName("name")
    var name = ""
    fun name() = name

    @SerializedName("description")
    var description=""
    fun description() = description

    @SerializedName("abv")
    var coordinates=""
    fun coordinates() = coordinates

    @SerializedName("id")
    var id = ""
    fun id() = id


}