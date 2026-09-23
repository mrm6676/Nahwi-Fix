package com.example.nahwifix.data.allam

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit API Service for Hugging Face Inference API
 * specifically interacting with https://huggingface.co/humain-ai/ALLaM-7B-Instruct-preview
 */
interface AllamApiService {

    @POST("models/{modelId}")
    suspend fun generateWithAllam(
        @Path(value = "modelId", encoded = true) modelId: String = "humain-ai/ALLaM-7B-Instruct-preview",
        @Header("Authorization") authorization: String? = null,
        @Body request: AllamInferenceRequest
    ): List<AllamInferenceResponseItem>

    @POST("v1/chat/completions")
    suspend fun chatWithAllam(
        @Header("Authorization") authorization: String? = null,
        @Body request: AllamChatRequest
    ): AllamChatResponse
}
