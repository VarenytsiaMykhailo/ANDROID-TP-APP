package com.example.app.datalayer.repositories

import com.example.app.BuildConfig

class LocalPropertiesSecrets {

    fun getBackendConnectionUrl(): String =
        BuildConfig.BACKEND_CONNECTION_URL
}