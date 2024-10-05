package ru.practicum.android.diploma.search.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val HH_URL = "https://api.hh.ru/"
    private const val AUTH_TOKEN = "access_token"
    private const val USER_AGENT = "user_agent"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(AUTH_TOKEN, USER_AGENT))
            .build()
    }

    val headHunterAPI: HeadHunterAPI by lazy {
        Retrofit.Builder()
            .baseUrl(HH_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HeadHunterAPI::class.java)
    }
}
