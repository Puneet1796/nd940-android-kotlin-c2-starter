package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {
    @GET("neo/rest/v1/feed")
    suspend fun getFeed(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): String

    @GET("planetary/apod")
    suspend fun getImageOfTheDay(): String
}

private val okHttpClient = OkHttpClient.Builder()
    .addNetworkInterceptor { chain ->
        val oldUrl = chain.request().url()
        val newUrl = oldUrl.newBuilder()
            .addEncodedQueryParameter("api-key", Constants.API_KEY)
            .build()
        val newRequest = chain.request().newBuilder()
            .url(newUrl)
            .build()
        chain.proceed(newRequest)
    }
    .build()

object Network {
    private val retrofit: Retrofit =
        Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(Constants.BASE_URL)
            .build()

    val apiService: NetworkService by lazy {
        retrofit.create(NetworkService::class.java)
    }
}