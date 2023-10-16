package com.example.app.datalayer.accessesors

import com.example.app.datalayer.models.Location
import com.example.app.datalayer.repositories.LocalPropertiesSecrets
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LocationsAccessor {
    //отредачить по апи
    @GET("places/list")
    suspend fun getLocations(): List<Location>
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
                val backendURL:LocalPropertiesSecrets?=null
                addConverterFactory(GsonConverterFactory.create())
                baseUrl("${ backendURL?.getBackendConnectionUrl()}")
            }.build()


            return retrofit.create(LocationsAccessor::class.java)
        }
    }
}