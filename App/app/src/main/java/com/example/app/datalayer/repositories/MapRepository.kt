package com.example.app.datalayer.repositories

import com.example.app.datalayer.models.Place
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

internal interface MapRepository {

    @GET("places/list")
    suspend fun getPlaces(): List<Place>

    companion object {

        val mapRepository: MapRepository = createMapRepository()

        private fun createMapRepository(): MapRepository {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder().apply {
                addNetworkInterceptor(loggingInterceptor)
            }.build()

            val retrofit = Retrofit.Builder().apply {
                client(client)
                addConverterFactory(GsonConverterFactory.create())
                baseUrl(LocalPropertiesSecretsRepository.backendConnectionUrl)
            }.build()

            return retrofit.create(MapRepository::class.java)
        }
    }

}
