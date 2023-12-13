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

    private const val RESPONSE_MAX_TOKENS_IN_TEXT = 280 // Num of tokens in response

    private const val LOG_TAG = "ChatGptRepository"

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
        Log.d(LOG_TAG, "formed request = $request")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(LOG_TAG, "API failed", e)
                onFailure()
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val body = response.body?.string()
                    if (body != null) {
                        val jsonObject = JSONObject(body)
                        val jsonArray: JSONArray = jsonObject.getJSONArray("choices")

                        val placeDescription = jsonArray
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")
                            .filterTextTail()
                        Log.v(LOG_TAG, "filteredTextResult = $placeDescription")
                        onSuccess(placeDescription)
                    } else {
                        Log.v(LOG_TAG, "response body is empty")
                        onFailure()
                    }
                } catch (e: Exception) {
                    Log.e(LOG_TAG, e.message, e)
                    onFailure()
                }
            }
        })
    }

    private fun String.filterTextTail(): String {
        var result = this
        var lastSymbol = result.last()
        while (lastSymbol != '.' && lastSymbol != '!' && lastSymbol != ';') {
            result = result.removeSuffix(lastSymbol.toString())
            lastSymbol = result.last()
        }

        return result
    }

    private fun getRequestBody(placeName: String) =
        """
            {
                "model": "gpt-3.5-turbo",
                "max_tokens": $RESPONSE_MAX_TOKENS_IN_TEXT,
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