package com.example.nahwifix.data.allam

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Request payload for Hugging Face Inference API / Serverless Router
 * for the ALLaM model: https://huggingface.co/humain-ai/ALLaM-7B-Instruct-preview
 * Supports both text-generation standard schema and OpenAI-compatible Chat Completions schema.
 */
@Serializable
data class AllamInferenceRequest(
    val inputs: String,
    val parameters: AllamParameters? = null
)

@Serializable
data class AllamParameters(
    @SerialName("max_new_tokens")
    val maxNewTokens: Int = 1024,
    val temperature: Float = 0.4f,
    @SerialName("top_p")
    val topP: Float = 0.9f,
    @SerialName("return_full_text")
    val returnFullText: Boolean = false
)

@Serializable
data class AllamInferenceResponseItem(
    @SerialName("generated_text")
    val generatedText: String? = null
)

/**
 * OpenAI-compatible chat completions schema for Hugging Face Inference API
 */
@Serializable
data class AllamChatRequest(
    val model: String = "humain-ai/ALLaM-7B-Instruct-preview",
    val messages: List<AllamChatMessage>,
    val temperature: Float = 0.4f,
    @SerialName("max_tokens")
    val maxTokens: Int = 1024,
    @SerialName("top_p")
    val topP: Float = 0.9f
)

@Serializable
data class AllamChatMessage(
    val role: String,
    val content: String
)

@Serializable
data class AllamChatResponse(
    val choices: List<AllamChatChoice> = emptyList()
)

@Serializable
data class AllamChatChoice(
    val message: AllamChatMessage? = null,
    val text: String? = null
)
