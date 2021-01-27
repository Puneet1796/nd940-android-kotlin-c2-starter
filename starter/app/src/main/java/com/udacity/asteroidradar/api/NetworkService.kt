package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import timber.log.Timber
import kotlin.jvm.Throws


interface NetworkService {
    @Throws(HttpException::class)
    @GET("neo/rest/v1/feed")
    suspend fun getFeed(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): String

    @Throws(HttpException::class)
    @GET("planetary/apod")
    suspend fun getImageOfTheDay(): String
}

var httpLoggingInterceptor = HttpLoggingInterceptor { message -> Timber.tag("OkHttp").d(message) }
    .apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(httpLoggingInterceptor)
    .addInterceptor { chain ->
        val original = chain.request()
        val originalHttpUrl = original.url

        val newHttpUrl = originalHttpUrl.newBuilder()
            .addQueryParameter("api_key", Constants.API_KEY)
            .build()

        val newRequest = original.newBuilder()
            .url(newHttpUrl)
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