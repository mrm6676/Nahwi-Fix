package com.example.nahwifix.data.gemini

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val tools: List<JsonObject>? = null,
    val systemInstruction: Content? = null
)

@Serializable
data class Content(
    val parts: List<Part>,
    val role: String? = null
)

@Serializable
data class Part(
    val text: String? = null
)

@Serializable
data class GenerationConfig(
    val responseMimeType: String? = null,
    val responseSchema: JsonObject? = null,
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate> = emptyList()
)

@Serializable
data class Candidate(
    val content: Content
)

/**
 * Structured output model parsed from Gemini's JSON response
 * for Arabic grammar and punctuation analysis.
 */
@Serializable
data class ArabicGrammarGeminiOutput(
    val errors: List<ArabicGrammarIssueDto> = emptyList(),
    val fullyCorrectedText: String = "",
    val generalNotes: String = ""
)

@Serializable
data class ArabicGrammarIssueDto(
    val originalSnippet: String,
    val suggestedReplacement: String,
    val category: String, // GRAMMAR, PUNCTUATION, SPELLING_HAMZA, AGREEMENT
    val issueDescriptionAr: String,
    val issueDescriptionEn: String,
    val explanationAr: String,
    val explanationEn: String,
    val ruleReference: String = ""
)
