package com.example.app.datalayer.repositories

import com.example.app.datalayer.repositories.interceptors.HeadersInterceptor
import com.example.app.datalayer.models.Place
import com.example.app.datalayer.models.PlaceDescription
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

internal interface MapRepository {

    @GET("places/list")
    suspend fun getPlaces(): List<Place>

    @GET("places/info")
    suspend fun getPlaceDescription(@Query("place_id") placeId: String): PlaceDescription

    companion object {

        val mapRepository: MapRepository = createMapRepository()

        private fun createMapRepository(): MapRepository {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder().apply {
                addNetworkInterceptor(loggingInterceptor)
                addInterceptor(HeadersInterceptor())
            }.build()

            val retrofit = Retrofit.Builder().apply {
                client(client)
                addConverterFactory(GsonConverterFactory.create())
                baseUrl(LocalPropertiesSecretsRepository.BACKEND_CONNECTION_URL)
            }.build()

            return retrofit.create(MapRepository::class.java)
        }
    }
}
