package com.example.nahwifix.data.allam

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

object AllamRetrofitClient {

    const val MODEL_ID = "humain-ai/ALLaM-7B-Instruct-preview"
    const val MODEL_URL = "https://huggingface.co/humain-ai/ALLaM-7B-Instruct-preview"
    private const val HF_INFERENCE_BASE_URL = "https://api-inference.huggingface.co/"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        OkHttpClient.Builder()
            .connectTimeout(45, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }

    val service: AllamApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(HF_INFERENCE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        retrofit.create(AllamApiService::class.java)
    }
}
