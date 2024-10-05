package ru.practicum.android.diploma.search.data.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val authToken: String?, private val userAgent: String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val requestWithHeaders = originalRequest.newBuilder()
            .header("Authorization", authToken ?: "")
            .header("User-Agent", userAgent ?: "")
            .build()

        return chain.proceed(requestWithHeaders)
    }
}
