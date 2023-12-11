package com.example.app.datalayer.repositories

import android.util.Log
import com.example.app.BuildConfig
import com.example.app.datalayer.repositories.interceptors.HeadersInterceptor
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

internal object ChatGptRepository {

    private const val responsePlaceDescriptionMaxTextSize = 1000 // Num of tokens in response

    private val client = OkHttpClient.Builder().apply {
        connectTimeout(20, TimeUnit.SECONDS)
        writeTimeout(20, TimeUnit.SECONDS)
        readTimeout(60, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            addNetworkInterceptor(loggingInterceptor)
        }
        addInterceptor(HeadersInterceptor())
    }.build()

    fun getPlaceDescriptionByChatGpt(
        placeName: String,
        onSuccess: (placeDescription: String) -> Unit,
        onFailure: () -> Unit,
    ) {
        val request = Request.Builder()
            .url(LocalPropertiesSecretsRepository.CHAT_GPT_CONNECTION_URL)
            .addHeader("Content-Type", "application/json")
            .addHeader(
                "Authorization",
                "Bearer ${LocalPropertiesSecretsRepository.CHAT_GPT_API_KEY}"
            )
            .post(getRequestBody(placeName).toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        Log.d("qwerty123", "formed request = $request")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("qwerty123", "API failed", e)
                onFailure()
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (body != null) {
                    Log.v("qwerty123", "body = $body")
                    val jsonObject = JSONObject(body)
                    val jsonArray: JSONArray = jsonObject.getJSONArray("choices")
                    Log.v("qwerty123", "jsonArray = $jsonArray")

                    val placeDescription = jsonArray.getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                    Log.v("qwerty123", "textResult = $placeDescription")

                    onSuccess(placeDescription)
                } else {
                    Log.v("qwerty123", "empty body")
                    onFailure()
                }
            }
        })
    }

    private fun getRequestBody(placeName: String) =
        """
            {
                "model": "gpt-3.5-turbo",
                "max_tokens": $responsePlaceDescriptionMaxTextSize,
                "messages": [
                    {
                        "role": "system",
                        "content": "Дай описание места"
                    },
                    {
                        "role": "user",
                        "content": "$placeName"
                    }
                ]
            }
        """.trimIndent()
}