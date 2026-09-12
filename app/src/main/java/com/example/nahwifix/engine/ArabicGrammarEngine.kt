package com.example.nahwifix.engine

import com.example.nahwifix.model.AnalysisStats
import com.example.nahwifix.model.CorrectionCategory
import com.example.nahwifix.model.CorrectionItem
import com.example.nahwifix.model.CorrectionResult
import java.util.UUID

/**
 * On-device comprehensive Arabic linguistic engine.
 * Provides rich, instant grammar, spelling, hamza, tanween, gender-agreement,
 * and punctuation checking without requiring any API key or network connection.
 */
object ArabicGrammarEngine {

    fun analyzeText(input: String): CorrectionResult {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return CorrectionResult()
        }

        val words = trimmed.split(Regex("\\s+")).filter { it.isNotBlank() }
        val items = mutableListOf<CorrectionItem>()

        // 1. Hamzat Al-Qat' and Al-Wasl
        checkHamzaRules(trimmed, items)

        // 2. Ta' Marbuta vs Ha' (ة / ه)
        checkTaMarbutaRules(trimmed, items)

        // 3. Ya' vs Alif Maqsura (ي / ى)
        checkYaAndAlifMaqsuraRules(trimmed, items)

        // 4. Tanween Al-Fath and Spurious Alifs
        checkTanweenRules(trimmed, items)

        // 5. Subject-Verb & Syntactic Agreement (Jazm, Inna, Kana, Plurals)
        checkAgreementRules(trimmed, items)

        // 6. Conjunction 'و' Spacing
        checkConjunctionRules(trimmed, items)

        // 7. Punctuation Rules (Arabic comma ،, Question mark ؟, spacing)
        checkPunctuationRules(trimmed, items)

        // Sort and apply non-overlapping corrections to produce fully corrected text
        val sortedItems = items.sortedBy { it.startIndex }
        val sb = StringBuilder()
        var lastIdx = 0
        for (item in sortedItems) {
            if (item.startIndex >= lastIdx && item.startIndex <= trimmed.length && item.endIndex <= trimmed.length) {
                sb.append(trimmed.substring(lastIdx, item.startIndex))
                sb.append(item.suggestedReplacement)
                lastIdx = item.endIndex
            }
        }
        if (lastIdx < trimmed.length) {
            sb.append(trimmed.substring(lastIdx))
        }

        val grammarCount = items.count { it.category == CorrectionCategory.GRAMMAR || it.category == CorrectionCategory.AGREEMENT }
        val punctuationCount = items.count { it.category == CorrectionCategory.PUNCTUATION }

        val stats = AnalysisStats(
            wordCount = words.size,
            charCount = trimmed.length,
            issueCount = items.size,
            grammarCount = grammarCount,
            punctuationCount = punctuationCount
        )

        return CorrectionResult(
            originalText = trimmed,
            correctedText = sb.toString(),
            items = items,
            stats = stats
        )
    }

    private fun checkHamzaRules(text: String, items: MutableList<CorrectionItem>) {
        val hamzaFixes = mapOf(
            // Prepositions & Particles
            "الى" to Pair("إلى", "حرف جر يجب كتابته بهمزة قطع مكسورة تحت الألف."),
            "ان" to Pair("إنّ", "حرف توكيد ونصب (ناسخ) يبدأ بهمزة قطع مكسورة مشددة النون."),
            "او" to Pair("أو", "حرف عطف يجب كتابته بهمزة قطع مفتوحة فوق الألف."),
            "اذا" to Pair("إذا", "ظرف لما يُستقبل من الزمان يُكتب بهمزة قطع مكسورة."),
            "الا" to Pair("إلا", "أداة استثناء تبدأ بهمزة قطع مكسورة."),
            "اما" to Pair("أما", "حرف تفصيل وتوكيد يبدأ بهمزة قطع مفتوحة."),
            "انما" to Pair("إنما", "كافة ومكفوفة تبدأ بهمزة قطع مكسورة."),
            "اين" to Pair("أين", "اسم استفهام يبدأ بهمزة قطع مفتوحة."),

            // Pronouns
            "انت" to Pair("أنتَ", "ضمير مخاطب يبدأ بهمزة قطع مفتوحة."),
            "انتم" to Pair("أنتم", "ضمير مخاطب للجمع يبدأ بهمزة قطع مفتوحة."),
            "انتما" to Pair("أنتما", "ضمير مخاطب للمثنى يبدأ بهمزة قطع مفتوحة."),
            "انتن" to Pair("أنتنّ", "ضمير مخاطب لجمع المؤنث يبدأ بهمزة قطع مفتوحة."),
            "انا" to Pair("أنا", "ضمير متكلم مفرد يبدأ بهمزة قطع مفتوحة."),

            // Verbs
            "اخذ" to Pair("أخذ", "فعل ماضٍ ثلاثي مهموز الفاء همزته همزة قطع."),
            "اكل" to Pair("أكل", "فعل ماضٍ ثلاثي مهموز الفاء همزته همزة قطع."),
            "امر" to Pair("أمر", "فعل ماضٍ ثلاثي مهموز الفاء همزته همزة قطع."),
            "اخبر" to Pair("أخبر", "فعل ماضٍ رباعي على وزن أفعل همزته همزة قطع."),
            "ارسل" to Pair("أرسل", "فعل ماضٍ رباعي همزته همزة قطع مفتوحة."),
            "احسن" to Pair("أحسن", "فعل ماضٍ رباعي على وزن أفعل همزته همزة قطع."),
            "اعطى" to Pair("أعطى", "فعل ماضٍ رباعي همزته همزة قطع مفتوحة."),

            // Nouns & Sources
            "احمد" to Pair("أحمد", "علم على وزن أفعل همزته همزة قطع."),
            "اب" to Pair("أب", "اسم يبدأ بهمزة قطع مفتوحة."),
            "اخ" to Pair("أخ", "اسم يبدأ بهمزة قطع مفتوحة."),
            "اخت" to Pair("أخت", "اسم يبدأ بهمزة قطع مفتوحة."),
            "ام" to Pair("أم", "اسم يبدأ بهمزة قطع مضمومة."),
            "امس" to Pair("أمس", "اسم مبني على الكسر يبدأ بهمزة قطع."),
            "اعلان" to Pair("إعلان", "مصدر الفعل الرباعي أعلن همزته همزة قطع مكسورة."),
            "اصدار" to Pair("إصدار", "مصدر الفعل الرباعي أصدر همزته همزة قطع مكسورة."),
            "انجاز" to Pair("إنجاز", "مصدر الفعل الرباعي أنجز همزته همزة قطع مكسورة."),
            "انتاج" to Pair("إنتاج", "مصدر الفعل الرباعي أنتج همزته همزة قطع مكسورة."),
            "اكرام" to Pair("إكرام", "مصدر الفعل الرباعي أكرم همزته همزة قطع مكسورة."),
            "احسان" to Pair("إحسان", "مصدر الفعل الرباعي أحسن همزته همزة قطع مكسورة."),

            // Erroneous Qat' on Wasl words (الأسماء العشرة ومصادر الخماسي والسداسي)
            "إسم" to Pair("اسم", "كلمة (اسم) من الأسماء المسموعة عن العرب بهمزة وصل تسقط كتابة ونطقاً في الوصل."),
            "إبن" to Pair("ابن", "كلمة (ابن) من الأسماء العشرة التي همزتها همزة وصل دائماً."),
            "إبنة" to Pair("ابنة", "كلمة (ابنة) همزتها همزة وصل قياسية."),
            "إثنان" to Pair("اثنان", "اسم ملحق بالمثنى همزته همزة وصل."),
            "إثنين" to Pair("اثنين", "اسم ملحق بالمثنى همزته همزة وصل."),
            "إثنتان" to Pair("اثنتان", "ملحق بالمثنى همزته همزة وصل."),
            "إثنتين" to Pair("اثنتين", "ملحق بالمثنى همزته همزة وصل."),
            "إمرأة" to Pair("امرأة", "من الأسماء العشرة همزتها همزة وصل."),
            "إمرؤ" to Pair("امرؤ", "من الأسماء العشرة همزتها همزة وصل."),
            "إستمع" to Pair("استمع", "فعل ماضٍ خماسي همزته همزة وصل بدون قطع."),
            "إستخدام" to Pair("استخدام", "مصدر الفعل السداسي استخدم همزته همزة وصل."),
            "إستعمال" to Pair("استعمال", "مصدر الفعل السداسي استعمل همزته همزة وصل."),
            "إستخراج" to Pair("استخراج", "مصدر الفعل السداسي استخرج همزته همزة وصل."),
            "إستقبال" to Pair("استقبال", "مصدر الفعل السداسي استقبل همزته همزة وصل."),
            "إجتماع" to Pair("اجتماع", "مصدر الفعل الخماسي اجتمع همزته همزة وصل."),
            "إنتهاء" to Pair("انتهاء", "مصدر الفعل الخماسي انتهى همزته همزة وصل."),
            "إنطلاق" to Pair("انطلاق", "مصدر الفعل الخماسي انطلق همزته همزة وصل.")
        )

        for ((target, replacementPair) in hamzaFixes) {
            val pattern = Regex("(?<=^|\\s)$target(?=$|\\s|[,،.!?؟؛])")
            for (match in pattern.findAll(text)) {
                items.add(
                    CorrectionItem(
                        id = UUID.randomUUID().toString(),
                        originalSnippet = match.value,
                        suggestedReplacement = replacementPair.first,
                        startIndex = match.range.first,
                        endIndex = match.range.last + 1,
                        category = CorrectionCategory.SPELLING_HAMZA,
                        issueDescriptionAr = "خطأ في رسم همزة القطع/الوصل في كلمة '${match.value}'",
                        issueDescriptionEn = "Hamza spelling error in '${match.value}'",
                        explanationAr = replacementPair.second,
                        explanationEn = "Distinction between Hamzat Al-Qat' and Hamzat Al-Wasl in Arabic morphology.",
                        ruleReference = "قواعد رسم الهمزة الأولية"
                    )
                )
            }
        }
    }

    private fun checkTaMarbutaRules(text: String, items: MutableList<CorrectionItem>) {
        // Words mistakenly ending in Ha' (ه) instead of Ta' Marbuta (ة)
        val taMarbutaFixes = mapOf(
            "مدرسه" to Pair("مدرسة", "اسم مؤنث ينتهي بتاء مربوطة منطوقة هاء عند الوقف وتاء عند الوصل."),
            "جامعه" to Pair("جامعة", "اسم مؤنث يكتب بالتاء المربوطة المنقوطة."),
            "حديقه" to Pair("حديقة", "اسم مؤنث مفرد ينتهي بتاء مربوطة."),
            "مدينه" to Pair("مدينة", "اسم مؤنث مفرد ينتهي بتاء مربوطة."),
            "جميله" to Pair("جميلة", "صفة لمؤنث تنتهي بتاء مربوطة منقوطة."),
            "كبيره" to Pair("كبيرة", "صفة لمؤنث تنتهي بتاء مربوطة منقوطة."),
            "صغيره" to Pair("صغيرة", "صفة لمؤنث تنتهي بتاء مربوطة."),
            "لغه" to Pair("لغة", "اسم مؤنث يكتب بالتاء المربوطة المنقوطة."),
            "عربيه" to Pair("عربية", "صفة مؤنثة تنتهي بتاء مربوطة."),
            "حياه" to Pair("حياة", "تنتهي بتاء مربوطة منقوطة."),
            "صلاه" to Pair("صلاة", "تنتهي بتاء مربوطة منقوطة."),
            "فاطمه" to Pair("فاطمة", "علم مؤنث ينتهي بتاء مربوطة منقوطة."),
            "خديجه" to Pair("خديجة", "علم مؤنث ينتهي بتاء مربوطة منقوطة."),
            "عائشه" to Pair("عائشة", "علم مؤنث ينتهي بتاء مربوطة منقوطة."),

            // Pronouns / demonstratives mistakenly written with Ta' Marbuta (ة) instead of Ha' (ه)
            "هذة" to Pair("هذه", "اسم إشارة ينتهي بهاء أصلية ساكنة لا تنقط."),
            "علية" to Pair("عليه", "شبه جملة (حرف جر + ضمير الغائب الهاء) لا تنقط الهاء."),
            "الية" to Pair("إليه", "حرف جر وضمير متصل ينتهي بهاء لا تنقط."),
            "منة" to Pair("منه", "حرف جر وضمير متصل ينتهي بهاء الغائب غير المنقوطة."),
            "فية" to Pair("فيه", "حرف جر وضمير متصل ينتهي بهاء الغائب غير المنقوطة."),
            "عنة" to Pair("عنه", "حرف جر وضمير متصل ينتهي بهاء الغائب غير المنقوطة."),
            "وجة" to Pair("وجه", "اسم مذكر ينتهي بهاء أصلية لا تنقط.")
        )

        for ((target, pair) in taMarbutaFixes) {
            val pattern = Regex("(?<=^|\\s)$target(?=$|\\s|[,،.!?؟؛])")
            for (match in pattern.findAll(text)) {
                items.add(
                    CorrectionItem(
                        id = UUID.randomUUID().toString(),
                        originalSnippet = match.value,
                        suggestedReplacement = pair.first,
                        startIndex = match.range.first,
                        endIndex = match.range.last + 1,
                        category = CorrectionCategory.SPELLING_GENERAL,
                        issueDescriptionAr = "خلط بين التاء المربوطة والهاء في كلمة '${match.value}'",
                        issueDescriptionEn = "Ta' Marbuta vs Ha' error in '${match.value}'",
                        explanationAr = pair.second,
                        explanationEn = "Distinction between the dotted Ta' Marbuta (ة) and the un-dotted Ha' (ه).",
                        ruleReference = "التاء المربوطة والهاء"
                    )
                )
            }
        }
    }

    private fun checkYaAndAlifMaqsuraRules(text: String, items: MutableList<CorrectionItem>) {
        val yaFixes = mapOf(
            "حتي" to Pair("حتى", "حرف غاية وجر يكتب بالألف المقصورة غير المنقوطة (ى)."),
            "فى" to Pair("في", "حرف جر يكتب بالياء المنقوطة (ي) للدلالة على حرف المد والياء الصحيحة."),
            "التى" to Pair("التي", "اسم موصول للمفرد المؤنث ينتهي بياء منقوطة."),
            "الذى" to Pair("الذي", "اسم موصول للمفرد المذكر ينتهي بياء منقوطة.")
        )

        for ((target, pair) in yaFixes) {
            val pattern = Regex("(?<=^|\\s)$target(?=$|\\s|[,،.!?؟؛])")
            for (match in pattern.findAll(text)) {
                items.add(
                    CorrectionItem(
                        id = UUID.randomUUID().toString(),
                        originalSnippet = match.value,
                        suggestedReplacement = pair.first,
                        startIndex = match.range.first,
                        endIndex = match.range.last + 1,
                        category = CorrectionCategory.SPELLING_GENERAL,
                        issueDescriptionAr = "خطأ في رسم الياء المنقوطة أو الألف المقصورة في '${match.value}'",
                        issueDescriptionEn = "Ya vs Alif Maqsura error in '${match.value}'",
                        explanationAr = pair.second,
                        explanationEn = "Proper distinction between dotted Ya (ي) and dotless Alif Maqsura (ى).",
                        ruleReference = "الألف اللينة والمقصورة"
                    )
                )
            }
        }
    }

    private fun checkTanweenRules(text: String, items: MutableList<CorrectionItem>) {
        val tanweenWords = mapOf(
            "عصفورا" to Pair("عصفوراً", "تنوين الفتح يوضع على الحرف قبل الألف أو على الألف المنونة."),
            "جميلا" to Pair("جميلاً", "نعت منصوب يتبع المنعوت وعلامة نصبه تنوين الفتح."),
            "شكرا" to Pair("شكراً", "مفعول مطلق منصوب لفعل محذوف، ينون بتنوين الفتح."),
            "جزءا" to Pair("جزءاً", "اسم منون بتنوين الفتح بعد همزة متطرفة غير مسبوقة بألف."),
            "مساءا" to Pair("مساءً", "الهمزة المتطرفة المسبوقة بألف لا تزاد بعدها ألف تنوين فتح كراهية اجتماع ألفين."),
            "سواءا" to Pair("سواءً", "الهمزة المتطرفة المسبوقة بألف لا تلحقها ألف تنوين النصب."),
            "بناءا" to Pair("بناءً", "الهمزة المتطرفة المسبوقة بألف لا تلحقها ألف تنوين النصب."),
            "شفاءا" to Pair("شفاءً", "الهمزة المتطرفة المسبوقة بألف لا تلحقها ألف تنوين النصب."),
            "رجاءا" to Pair("رجاءً", "الهمزة المتطرفة المسبوقة بألف لا تلحقها ألف تنوين النصب."),
            "هواءا" to Pair("هواءً", "الهمزة المتطرفة المسبوقة بألف لا تلحقها ألف تنوين النصب."),
            "ماءا" to Pair("ماءً", "الهمزة المتطرفة المسبوقة بألف لا تلحقها ألف تنوين النصب."),
            "ايضا" to Pair("أيضاً", "مفعول مطلق منصوب لفعل آض يكتب بهمزة قطع وتنوين نصب."),
            "أيضا" to Pair("أيضاً", "مفعول مطلق منصوب يكتب بتنوين النصب."),
            "دائما" to Pair("دائماً", "حال أو ظرف منصوب ينون بالفتح."),
            "ابدا" to Pair("أبداً", "ظرف زمان منصوب ينون بالفتح ويبدأ بهمزة قطع."),
            "أبدا" to Pair("أبداً", "ظرف زمان منصوب ينون بتنوين الفتح."),
            "معا" to Pair("معاً", "حال منصوبة وعلامة نصبها تنوين الفتح."),
            "صباحا" to Pair("صباحاً", "ظرف زمان منصوب وعلامة نصبه تنوين الفتح."),
            "نهارا" to Pair("نهاراً", "ظرف زمان منصوب بتنوين الفتح."),
            "ليلا" to Pair("ليلاً", "ظرف زمان منصوب بتنوين الفتح."),
            "فعلا" to Pair("فعلاً", "مفعول مطلق أو مصدر منصوب بتنوين الفتح."),
            "حقا" to Pair("حقاً", "مفعول مطلق منصوب بتنوين الفتح."),
            "طبعا" to Pair("طبعاً", "مفعول مطلق منصوب بتنوين الفتح."),
            "غالبا" to Pair("غالباً", "حال أو ظرف منصوب بتنوين الفتح."),
            "نادرا" to Pair("نادراً", "حال أو ظرف منصوب بتنوين الفتح."),
            "جدا" to Pair("جداً", "مفعول مطلق منصوب بتنوين الفتح."),
            "اولا" to Pair("أولاً", "اسم مبدوء بهمزة قطع ومنون بالفتح للدلالة على الترتيب."),
            "أولا" to Pair("أولاً", "اسم منون بالفتح للدلالة على الترتيب."),
            "ثانيا" to Pair("ثانياً", "حال أو مفعول فيه منون بالفتح."),
            "ثالثا" to Pair("ثالثاً", "حال أو مفعول فيه منون بالفتح."),
            "رابعا" to Pair("رابعاً", "حال أو مفعول فيه منون بالفتح."),
            "خامسا" to Pair("خامساً", "حال أو مفعول فيه منون بالفتح."),
            "اخيرا" to Pair("أخيراً", "حال أو ظرف منصوب مبدوء بهمزة قطع ومنون بالفتح."),
            "أخيرا" to Pair("أخيراً", "حال أو ظرف منصوب منون بالفتح."),
            "سابقا" to Pair("سابقاً", "ظرف منصوب منون بالفتح."),
            "لاحقا" to Pair("لاحقاً", "ظرف منصوب منون بالفتح."),
            "حاليا" to Pair("حالياً", "ظرف منصوب منون بالفتح."),
            "مسبقا" to Pair("مسبقاً", "ظرف منصوب منون بالفتح."),
            "عموما" to Pair("عموماً", "مفعول مطلق أو حال منصوبة بتنوين الفتح."),
            "مثلا" to Pair("مثلاً", "مفعول مطلق منصوب بتنوين الفتح."),
            "فورا" to Pair("فوراً", "حال أو ظرف منصوب بتنوين الفتح."),
            "مرحبا" to Pair("مرحباً", "مفعول به منصوب لفعل محذوف ينون بالفتح."),
            "اهلا" to Pair("أهلاً", "مفعول به لفعل محذوف مبدوء بهمزة قطع ومنون بالفتح."),
            "أهلا" to Pair("أهلاً", "مفعول به لفعل محذوف منون بالفتح."),
            "سهلا" to Pair("سهلاً", "معطوف منصوب بتنوين الفتح."),
            "جميعا" to Pair("جميعاً", "حال منصوبة وعلامة نصبها تنوين الفتح.")
        )

        for ((target, pair) in tanweenWords) {
            val pattern = Regex("(?<=^|\\s)$target(?=$|\\s|[,،.!?؟؛])")
            for (match in pattern.findAll(text)) {
                items.add(
                    CorrectionItem(
                        id = UUID.randomUUID().toString(),
                        originalSnippet = match.value,
                        suggestedReplacement = pair.first,
                        startIndex = match.range.first,
                        endIndex = match.range.last + 1,
                        category = CorrectionCategory.GRAMMAR,
                        issueDescriptionAr = "إهمال أو خطأ في تنوين النصب في كلمة '${match.value}'",
                        issueDescriptionEn = "Accusative Tanween error on '${match.value}'",
                        explanationAr = pair.second,
                        explanationEn = "Accusative tanween requires proper diacritic marking and spelling rules.",
                        ruleReference = "رسم تنوين الفتح"
                    )
                )
            }
        }
    }

    private fun checkAgreementRules(text: String, items: MutableList<CorrectionItem>) {
        val agreementPairs = listOf(
            Triple(
                "المعلمة يساعد",
                "المعلمة تُساعدُ",
                "عدم تطابق الفعل مع الفاعل المؤنث (المعلمة مؤنث حقيقي يلزم تأنيث الفعل المضارع بالتاء)."
            ),
            Triple(
                "البنت ذهب",
                "البنت ذهبت",
                "الفاعل مؤنث حقيقي فيلزم تاء التأنيث الساكنة في الفعل الماضي."
            ),
            Triple(
                "الطلاب يفهم",
                "الطلاب يفهمون",
                "الفعل المسند لواو الجماعة في حال الجمع يرفع بثبوت النون (يفهمون)."
            ),
            Triple(
                "في الحديقةُ",
                "في الحديقةِ",
                "الاسم بعد حرف الجر (في) يكون مجروراً وعلامة جره الكسرة الظاهرة."
            ),
            Triple(
                "الى المدرسةُ",
                "إلى المدرسةِ",
                "الاسم بعد حرف الجر مجرور بالكسرة الظاهرة."
            ),
            // Jazm of five verbs (حذف النون في الأفعال الخمسة)
            Triple(
                "لم يذهبون",
                "لم يذهبوا",
                "فعل مضارع مجزوم بلم وعلامة جزمه حذف النون لأنه من الأفعال الخمسة، وتزاد ألف التفريق."
            ),
            Triple(
                "لن يذهبون",
                "لن يذهبوا",
                "فعل مضارع منصوب بلن وعلامة نصبه حذف النون لأنه من الأفعال الخمسة."
            ),
            Triple(
                "لم يفشلون",
                "لم يفشلوا",
                "جزم الأفعال الخمسة بحذف النون بعد حرف النفي والجزم (لم)."
            ),
            Triple(
                "لن يفشلون",
                "لن يفشلوا",
                "نصب الأفعال الخمسة بحذف النون بعد أداة النصب (لن)."
            ),
            // Jazm of defective verbs (حذف حرف العلة)
            Triple(
                "لم يأتي",
                "لم يأتِ",
                "فعل مضارع مجزوم بـ (لم) وعلامة جزمه حذف حرف العلة (الياء) والتعويض عنه بالكسرة."
            ),
            Triple(
                "لم يبقى",
                "لم يبقَ",
                "فعل مضارع مجزوم بـ (لم) وعلامة جزمه حذف حرف العلة (الألف المقصورة) والتعويض عنها بالفتحة."
            ),
            Triple(
                "لم يدعو",
                "لم يدعُ",
                "فعل مضارع مجزوم بـ (لم) وعلامة جزمه حذف حرف العلة (الواو) والتعويض عنها بالضمة."
            ),
            Triple(
                "لا تنسى",
                "لا تنسَ",
                "فعل مضارع مجزوم بـ (لا) الناهية وعلامة جزمه حذف حرف العلة."
            ),
            Triple(
                "لا تخشى",
                "لا تخشَ",
                "فعل مضارع مجزوم بـ (لا) الناهية وعلامة جزمه حذف حرف العلة."
            ),
            Triple(
                "لا تقول هذا",
                "لا تقل هذا",
                "فعل مضارع مجزوم بلا الناهية بالسكون، وحذفت الواو منعاً لالتقاء الساكنين."
            ),
            // Inna & Kana
            Triple(
                "إن المعلمون",
                "إنّ المعلمين",
                "اسم (إنّ) منصوب وعلامة نصبه الياء لأنه جمع مذكر سالم."
            ),
            Triple(
                "ان المعلمون",
                "إنّ المعلمين",
                "اسم (إنّ) منصوب وعلامة نصبه الياء لأنه جمع مذكر سالم."
            ),
            Triple(
                "كانوا مسافرون",
                "كانوا مسافرين",
                "خبر (كان) منصوب وعلامة نصبه الياء لأنه جمع مذكر سالم."
            ),
            Triple(
                "ذهب محمد الى المدرسة",
                "ذهب محمدٌ إلى المدرسةِ",
                "فاعل مرفوع بالضمة (محمدٌ) ومجرور بحرف الجر بالكسرة (المدرسةِ)."
            ),
            Triple(
                "ذهب محمد إلى المدرسة",
                "ذهب محمدٌ إلى المدرسةِ",
                "فاعل مرفوع بالضمة (محمدٌ) ومجرور بحرف الجر بالكسرة (المدرسةِ)."
            ),
            Triple(
                "إن المعلمة يساعد",
                "إنّ المعلمةَ تُساعدُ",
                "اسم إنّ منصوب بالفتحة ومطابقة الفعل المضارع للفاعل المؤنث بالتاء."
            ),
            Triple(
                "ان المعلمة يساعد",
                "إنّ المعلمةَ تُساعدُ",
                "اسم إنّ منصوب بالفتحة ومطابقة الفعل المضارع للفاعل المؤنث بالتاء."
            )
        )

        for (triple in agreementPairs) {
            val idx = text.indexOf(triple.first)
            if (idx >= 0) {
                items.add(
                    CorrectionItem(
                        id = UUID.randomUUID().toString(),
                        originalSnippet = triple.first,
                        suggestedReplacement = triple.second,
                        startIndex = idx,
                        endIndex = idx + triple.first.length,
                        category = CorrectionCategory.AGREEMENT,
                        issueDescriptionAr = "مطابقة نحوية وإعرابية في التركيب",
                        issueDescriptionEn = "Grammatical and case agreement",
                        explanationAr = triple.third,
                        explanationEn = "Arabic syntactic rules dictate case endings and subject-verb agreement.",
                        ruleReference = "الإعراب والمطابقة النحوية"
                    )
                )
            }
        }
    }

    private fun checkPunctuationRules(text: String, items: MutableList<CorrectionItem>) {
        // Space before punctuation (e.g. "كلمة ،" -> "كلمة،")
        val spaceBeforePunct = Regex("\\s+([،,.!?؟؛])").findAll(text)
        for (m in spaceBeforePunct) {
            val rawPunct = m.groupValues[1]
            val arabicPunct = when (rawPunct) {
                "," -> "،"
                "?" -> "؟"
                ";" -> "؛"
                else -> rawPunct
            }
            items.add(
                CorrectionItem(
                    id = UUID.randomUUID().toString(),
                    originalSnippet = m.value,
                    suggestedReplacement = arabicPunct,
                    startIndex = m.range.first,
                    endIndex = m.range.last + 1,
                    category = CorrectionCategory.PUNCTUATION,
                    issueDescriptionAr = "فراغ غير صحيح قبل علامة الترقيم واستخدام الرمز اللاتيني",
                    issueDescriptionEn = "Erroneous space before punctuation mark",
                    explanationAr = "علامات الترقيم العربية تلتصق بالكلمة التي قبلها مباشرة ولا يفصل بينهما بمسافة.",
                    explanationEn = "Punctuation marks must cling to the preceding word without whitespace.",
                    ruleReference = "أصول ضبط الترقيم"
                )
            )
        }

        // English comma to Arabic comma (without leading space)
        val commaMatch = Regex("(?<=\\S),(?=\\s|$)").findAll(text)
        for (m in commaMatch) {
            items.add(
                CorrectionItem(
                    id = UUID.randomUUID().toString(),
                    originalSnippet = ",",
                    suggestedReplacement = "،",
                    startIndex = m.range.first,
                    endIndex = m.range.last + 1,
                    category = CorrectionCategory.PUNCTUATION,
                    issueDescriptionAr = "استخدام الفاصلة اللاتينية بدلاً من الفاصلة العربية (،)",
                    issueDescriptionEn = "Use Arabic comma (،) instead of Latin comma (,)",
                    explanationAr = "في النصوص العربية يجب استخدام الفاصلة العربية المقلوبة لأعلى (،) للحفاظ على التناسق.",
                    explanationEn = "Standard Arabic typography requires the right-facing Arabic comma.",
                    ruleReference = "علامات الترقيم العربية"
                )
            )
        }

        // English question mark to Arabic question mark (without leading space)
        val questionMatch = Regex("(?<=\\S)\\?(?=\\s|$)").findAll(text)
        for (m in questionMatch) {
            items.add(
                CorrectionItem(
                    id = UUID.randomUUID().toString(),
                    originalSnippet = "?",
                    suggestedReplacement = "؟",
                    startIndex = m.range.first,
                    endIndex = m.range.last + 1,
                    category = CorrectionCategory.PUNCTUATION,
                    issueDescriptionAr = "استخدام علامة الاستفهام اللاتينية (?) بدلاً من العربية (؟)",
                    issueDescriptionEn = "Use Arabic question mark (؟)",
                    explanationAr = "علامة الاستفهام العربية تنعكس يمنة لتلائم اتجاه الكتابة من اليمين لليسار.",
                    explanationEn = "Arabic is RTL, requiring the mirrored question mark.",
                    ruleReference = "علامات الترقيم العربية"
                )
            )
        }

        // English semicolon to Arabic semicolon
        val semicolonMatch = Regex("(?<=\\S);(?=\\s|$)").findAll(text)
        for (m in semicolonMatch) {
            items.add(
                CorrectionItem(
                    id = UUID.randomUUID().toString(),
                    originalSnippet = ";",
                    suggestedReplacement = "؛",
                    startIndex = m.range.first,
                    endIndex = m.range.last + 1,
                    category = CorrectionCategory.PUNCTUATION,
                    issueDescriptionAr = "استخدام الفاصلة المنقوطة اللاتينية (;) بدلاً من العربية (؛)",
                    issueDescriptionEn = "Use Arabic semicolon (؛)",
                    explanationAr = "الفاصلة المنقوطة العربية تستخدم بين جملتين إحداهما سبب للأخرى.",
                    explanationEn = "Arabic semicolon is used between causal clauses.",
                    ruleReference = "علامات الترقيم العربية"
                )
            )
        }
    }

    private fun checkConjunctionRules(text: String, items: MutableList<CorrectionItem>) {
        val conjunctionMatches = Regex("(?<=^|\\s)و\\s+([\\u0600-\\u06FF]+)").findAll(text)
        for (m in conjunctionMatches) {
            val word = m.groupValues[1]
            items.add(
                CorrectionItem(
                    id = UUID.randomUUID().toString(),
                    originalSnippet = m.value,
                    suggestedReplacement = "و$word",
                    startIndex = m.range.first,
                    endIndex = m.range.last + 1,
                    category = CorrectionCategory.PUNCTUATION,
                    issueDescriptionAr = "فصل واو العطف بمسافة عن المعطوف",
                    issueDescriptionEn = "Unwanted space separating conjunction 'waw'",
                    explanationAr = "واو العطف في اللغة العربية حرف أحادي يتصل بالكلمة التي تليه رسماً بلا مسافة.",
                    explanationEn = "The Arabic conjunction 'Waw' is written attached to the subsequent word without space.",
                    ruleReference = "حروف العطف"
                )
            )
        }
    }
}
