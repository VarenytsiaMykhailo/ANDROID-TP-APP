package com.example.app.datalayer.models

import com.google.gson.annotations.SerializedName

data class CategoriesList(

    @SerializedName("categories")
    val categories: List<String>
)