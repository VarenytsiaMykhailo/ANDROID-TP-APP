package com.example.app.datalayer.repositories

import com.example.app.BuildConfig


internal object LocalPropertiesSecretsRepository {

    const val BACKEND_CONNECTION_URL: String = BuildConfig.BACKEND_CONNECTION_URL

    const val MAPS_API_KEY: String = BuildConfig.MAPS_API_KEY

    lateinit var USER_UUID: String // Initializes from MainActivity

    lateinit var APP_PACKAGE_NAME: String // Initializes from MainActivity
}
