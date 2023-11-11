package com.example.app.datalayer.repositories

import com.example.app.BuildConfig
import com.example.app.datalayer.models.NearbyPlace
import com.example.app.datalayer.models.PlaceDescription
import com.example.app.datalayer.models.PlaceReaction
import com.example.app.datalayer.repositories.interceptors.HeadersInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

internal interface MapRepository {

    /**
     * @param location Example: "55.7520233,37.6174994" in lat, lng format.
     * @param radius Example: "50000" in metres.
     * @param limit Example: 10.
     * @param offset Example: 10.
     */
    @GET("suggest/nearby")
    suspend fun getSuggestPlaces(
        @Query("location") location: String,
        @Query("radius") radius: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): List<NearbyPlace>

    /**
     * @param placeId Example: "ChIJfRJDflpKtUYRl0UbgcrmUUk".
     */
    @GET("places/info")
    suspend fun getPlaceDescription(
        @Query("place_id") placeId: String,
    ): PlaceDescription

    @POST("suggest/reaction")
    suspend fun postSuggestReaction(
        @Body placeReaction: PlaceReaction,
    )

    @POST("suggest/user/new")
    suspend fun postSuggestUserNew() // User UUID stores in request header

    companion object {

        val mapRepository: MapRepository = createMapRepository()

        private fun createMapRepository(): MapRepository {
            val client = OkHttpClient.Builder().apply {
                if (BuildConfig.DEBUG) {
                    val loggingInterceptor = HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                    addNetworkInterceptor(loggingInterceptor)
                }
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
