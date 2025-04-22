package com.segunfrancis.remote

import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

val remoteModule = module {

    single<Json> {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
            prettyPrint = true
        }
    }

    single<HttpLoggingInterceptor> {
        HttpLoggingInterceptor().setLevel(
            HttpLoggingInterceptor.Level.BODY
        )
    }

    single<Interceptor> {
        Interceptor { response ->
            val request = response.request()
            val newUrl = request.url.newBuilder()
                .addQueryParameter("client_id", BuildConfig.ACCESS_KEY)
                .build()
            val newRequest = request.newBuilder()
                .header("Accept-Version", "v1")
                .url(newUrl)
                .build()
            response.proceed(newRequest)
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<Interceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .callTimeout(2L, TimeUnit.MINUTES)
            .build()
    }

    single {
        Retrofit.Builder()
            .client(get())
            .baseUrl("https://api.unsplash.com/")
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }
}
