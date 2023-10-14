package com.example.app.datalayer.Accessesors

import com.example.app.datalayer.Models.Location
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LocationsAccessor {
    //отредачить по апи
    @GET("/v2/beers")
    suspend fun getLocations(@Query("abv_lt") limit: Int): List<Location>
    @GET("/v2/beers/{id}")
    suspend fun getLocation(@Path("id") id: String): List<Location>

    companion object {
        fun create(): LocationsAccessor {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }


            val client = OkHttpClient.Builder().apply {
                addNetworkInterceptor(loggingInterceptor)
            }.build()


            val retrofit = Retrofit.Builder().apply {
                client(client)
                addConverterFactory(GsonConverterFactory.create())
                baseUrl("https://api.punkapi.com")
            }.build()


            return retrofit.create(LocationsAccessor::class.java)
        }
    }
}