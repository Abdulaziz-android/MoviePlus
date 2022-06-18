package com.abdulaziz.movieplus.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

import okhttp3.logging.HttpLoggingInterceptor

object ApiClient {

    const val BASE_URL = "https://api.themoviedb.org/3/"

    private fun getRetrofit() : Retrofit{
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService = getRetrofit().create(ApiService::class.java)

}