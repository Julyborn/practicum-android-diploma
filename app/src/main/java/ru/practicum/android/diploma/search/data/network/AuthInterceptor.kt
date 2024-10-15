package ru.practicum.android.diploma.search.data.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val authToken: String?, private val userAgent: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        requestBuilder.addHeader("userAgent", userAgent)

        authToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}
