package com.example.app.datalayer.repositories

import android.content.Context
import coil.ImageLoader
import coil.util.DebugLogger
import com.example.app.BuildConfig
import com.example.app.datalayer.repositories.interceptors.HeadersInterceptor
import okhttp3.OkHttpClient

internal fun getCoilImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .okHttpClient {
            OkHttpClient.Builder()
                .addNetworkInterceptor(HeadersInterceptor())
                .build()
        }
        .crossfade(true) // Pretty animation
        .apply {
            if (BuildConfig.DEBUG) {
                logger(DebugLogger())
            }
        }
        .build()
}