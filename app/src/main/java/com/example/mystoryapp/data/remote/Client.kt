package com.example.mystoryapp.data.remote

import androidx.viewbinding.BuildConfig
import com.example.mystoryapp.data.SharedPref
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Client(private val pref: SharedPref) {

    private val mainUrl = "https://story-api.dicoding.dev/v1/"
    private val loggingInterceptor = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    } else {
        HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.NONE)
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(Interceptor {chain ->
            val original = chain.request()

            val token = pref.readLoginInfo().toString()

            val request = original.newBuilder()
                .header("Authorization", "Bearer $token")
                .method(original.method, original.body)
                .build()

            chain.proceed(request)
        })
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(mainUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun instanceApi() : Api {
        return retrofit.create(Api::class.java)
    }
}