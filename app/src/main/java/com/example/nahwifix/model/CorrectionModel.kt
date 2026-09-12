package com.example.nahwifix.model

enum class CorrectionCategory(val titleAr: String, val titleEn: String) {
    GRAMMAR("مطابقة نحوية", "Grammar Agreement"),
    PUNCTUATION("علامات الترقيم", "Punctuation"),
    SPELLING_HAMZA("همزة وإملاء", "Spelling & Hamza"),
    SPELLING_GENERAL("إملاء ورسم الكلمات", "Spelling & Morphology"),
    AGREEMENT("توافق الفعل والفاعل", "Subject-Verb Agreement")
}

data class CorrectionItem(
    val id: String,
    val originalSnippet: String,
    val suggestedReplacement: String,
    val startIndex: Int,
    val endIndex: Int,
    val category: CorrectionCategory,
    val issueDescriptionAr: String,
    val issueDescriptionEn: String,
    val explanationAr: String,
    val explanationEn: String,
    val ruleReference: String = ""
)

data class AnalysisStats(
    val wordCount: Int = 0,
    val charCount: Int = 0,
    val issueCount: Int = 0,
    val grammarCount: Int = 0,
    val punctuationCount: Int = 0
)

data class CorrectionResult(
    val originalText: String = "",
    val correctedText: String = "",
    val items: List<CorrectionItem> = emptyList(),
    val stats: AnalysisStats = AnalysisStats()
)

data class PricingPlan(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val priceAr: String,
    val priceEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val featuresAr: List<String>,
    val featuresEn: List<String>,
    val isCurrent: Boolean = false,
    val isPopular: Boolean = false
)

enum class AppLanguage {
    ARABIC,
    ENGLISH
}
