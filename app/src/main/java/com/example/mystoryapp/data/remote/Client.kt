package com.example.mystoryapp.data.remote

import android.content.Context
import androidx.viewbinding.BuildConfig
import com.example.mystoryapp.data.SharedPref
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Client(context: Context) {

    private val mainUrl = "https://story-api.dicoding.dev/v1/"
    private val interceptor = HttpLoggingInterceptor()
        .apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
    private val httpClient = Interceptor { chain ->
        val original = chain.request()
        val sharedPref = SharedPref(context)
        val httpBuilder = original.url.newBuilder()

        val request = original.newBuilder()
            .method(original.method, original.body)
        if (sharedPref.readLoginInfo() != null) {
            val token = sharedPref.readLoginInfo()
            request.header("Authorization", "Bearer $token")
        }
        val endRequest = request.url(httpBuilder.build()).build()


        chain.proceed(endRequest)
    }


    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .addInterceptor(httpClient)
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