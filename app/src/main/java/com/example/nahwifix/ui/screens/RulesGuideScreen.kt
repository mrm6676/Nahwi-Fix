package com.example.nahwifix.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nahwifix.model.AppLanguage
import com.example.nahwifix.ui.NahwiFixViewModel

data class RuleGuideItem(
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val wrongExample: String,
    val rightExample: String
)

@Composable
fun RulesGuideScreen(
    viewModel: NahwiFixViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsState()
    val isAr = language == AppLanguage.ARABIC

    val rules = listOf(
        RuleGuideItem(
            titleAr = "همزتا الوصل والقطع",
            titleEn = "Hamzat Al-Wasl & Al-Qat'",
            descriptionAr = "همزة القطع تظهر نطقاً وكتابة (أ، إ، آ) مثل حروف الجر والأسماء (إلى، أو، أحمد). أما همزة الوصل فتسقط في درج الكلام (استمع، اكتب).",
            descriptionEn = "Hamzat Al-Qat' is always written and pronounced (أ, إ). Hamzat Al-Wasl connects phonetically without diacritic.",
            wrongExample = "ذهب محمد الى المدرسة",
            rightExample = "ذهب محمدٌ إلى المدرسةِ"
        ),
        RuleGuideItem(
            titleAr = "مطابقة الفعل للفاعل (تأنيث وتذكير)",
            titleEn = "Subject-Verb Agreement",
            descriptionAr = "يجب تأنيث الفعل إذا كان الفاعل اسماً ظاهراً مؤنثاً حقيقياً أو ضميراً يعود على مؤنث.",
            descriptionEn = "Arabic verbs must inflect according to gender, number, and person.",
            wrongExample = "المعلمة يساعد الطلاب",
            rightExample = "المعلمةُ تُساعدُ الطلاب"
        ),
        RuleGuideItem(
            titleAr = "رسم تنوين الفتح والألف الزائدة",
            titleEn = "Tanween Al-Fath and Alif",
            descriptionAr = "يُزاد ألف بعد تنوين الفتح في الأسماء المنونة (عصفوراً، جميلاً) ما لم ينته الاسم بتاء مربوطة أو همزة مسبوقة بألف (مساءً).",
            descriptionEn = "Tanween al-fath requires an accompanying Alif except on Ta Marbuta or Hamza preceded by Alif.",
            wrongExample = "شاهد عصفورا جميلا",
            rightExample = "شاهد عصفوراً جميلاً"
        ),
        RuleGuideItem(
            titleAr = "علامات الترقيم العربية وفواصل الجمل",
            titleEn = "Arabic Punctuation Rules",
            descriptionAr = "تُستخدم الفاصلة العربية (،) وعلامة الاستفهام (؟)، وتلتصق علامة الترقيم بالكلمة السابقة دون فراغ، وتوضع النقطة عند تمام المعنى.",
            descriptionEn = "Arabic comma (،) and question mark (؟) face rightwards matching RTL text flow.",
            wrongExample = "نص عربي , هل فهمت ?",
            rightExample = "نص عربي، هل فهمت؟"
        ),
        RuleGuideItem(
            titleAr = "واو العطف والاتصال بالكلمات",
            titleEn = "Conjunction Waw Spacing",
            descriptionAr = "واو العطف حرف أحادي يُكتب متصلاً بما بعده دون ترك مسافة بينه وبين الكلمة المعطوفة.",
            descriptionEn = "The Arabic conjunction 'Waw' is joined directly to the next word without space.",
            wrongExample = "ذهب و هو يحمل كتبه",
            rightExample = "ذهب وهو يحمل كتبه"
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("rules_guide_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isAr) "دليل القواعد النحوية والإملائية" else "Grammar & Punctuation Guide",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isAr) "استكشف أصول وقواعد الضبط النحوي المعتمدة في مصحح NahwiFix"
                else "Explore the authoritative Arabic grammar and punctuation rules applied by NahwiFix",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(rules) { rule ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) rule.titleAr else rule.titleEn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isAr) rule.descriptionAr else rule.descriptionEn,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (isAr) "خطأ شائع:" else "Incorrect:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = rule.wrongExample,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (isAr) "الصواب:" else "Correct:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = rule.rightExample,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
