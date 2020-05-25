package com.solarexsoft.learningrxjavademo

import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

/**
 * Created by houruhou on 2020/5/25/8:43 PM
 * Desc:
 */
 object NetHolder {
    private fun createClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .protocols(listOf(Protocol.HTTP_1_1))
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .connectionPool(ConnectionPool(5, 5, TimeUnit.SECONDS))
        return builder.build()
    }

    fun createRetrofit(): Retrofit {
        val builder = Retrofit.Builder()
        builder.callFactory(createClient())
            .addConverterFactory(JsonConvertFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

        try {
            builder.baseUrl("https://api.github.com")
        } catch (exception: Exception) {
        }

        return builder.build()
    }
}