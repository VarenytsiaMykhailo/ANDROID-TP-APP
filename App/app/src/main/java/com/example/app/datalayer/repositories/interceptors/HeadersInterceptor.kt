package com.example.app.datalayer.repositories.interceptors

import com.example.app.datalayer.repositories.LocalPropertiesSecretsRepository
import okhttp3.Interceptor
import okhttp3.Response

internal class HeadersInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url

        val request = chain.request().newBuilder()
            .url(url)
            .header(HEADER_PROXY_HEADER_KEY, HEADER_PROXY_HEADER_VALUE)
            .header(HEADER_X_UUID_KEY, LocalPropertiesSecretsRepository.USER_UUID)
        .build()

        return chain.proceed(request)
    }

    companion object {

        const val HEADER_PROXY_HEADER_KEY = "Proxy-Header"

        const val HEADER_PROXY_HEADER_VALUE = "go-explore"

        const val HEADER_X_UUID_KEY = "X-UUID"
    }
}