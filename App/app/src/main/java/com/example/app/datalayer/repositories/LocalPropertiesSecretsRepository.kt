package com.example.app.datalayer.repositories

import com.example.app.BuildConfig


internal object LocalPropertiesSecretsRepository {
    const val backendConnectionUrl: String = BuildConfig.BACKEND_CONNECTION_URL
}
