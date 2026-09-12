package com.example.nahwifix.ui.screens

import android.widget.Toast
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nahwifix.model.AppLanguage
import com.example.nahwifix.model.PricingPlan
import com.example.nahwifix.ui.NahwiFixViewModel

@Composable
fun PricingScreen(
    viewModel: NahwiFixViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.language.collectAsState()
    val isAr = language == AppLanguage.ARABIC

    val plans = listOf(
        PricingPlan(
            id = "basic",
            titleAr = "الخطة الأساسية (Basic)",
            titleEn = "Basic Free Plan",
            priceAr = "مجاناً دائماً",
            priceEn = "$0 / Forever Free",
            descriptionAr = "مثالية للطلاب والكتابة اليومية البسيطة والتدقيق السريع.",
            descriptionEn = "Ideal for students and everyday quick Arabic grammar checks.",
            featuresAr = listOf(
                "فحص حتى 1,000 كلمة في المرة الواحدة",
                "تدقيق القواعد النحوية الأساسية",
                "تصحيح علامات الترقيم العربية",
                "سجل التدقيق المحلي المحفوظ"
            ),
            featuresEn = listOf(
                "Up to 1,000 words per check",
                "Core Arabic grammar rules",
                "Punctuation mark correction",
                "Local history storage"
            ),
            isCurrent = true,
            isPopular = false
        ),
        PricingPlan(
            id = "pro",
            titleAr = "الخطة الاحترافية (Pro)",
            titleEn = "Pro Plan",
            priceAr = "$9 / شهرياً",
            priceEn = "$9 / month",
            descriptionAr = "للكتاب المحترفين، المترجمين، والصحفيين والمؤسسات التعليمية.",
            descriptionEn = "For professional writers, translators, and editors.",
            featuresAr = listOf(
                "فحص غير محدود للنصوص والكلمات",
                "تحليل نحوي عميق وتشكيل كامل بالحركات",
                "التعرف الضوئي على النصوص من الصور (OCR)",
                "توليد النطق الصوتي وقراءة النصوص المشكولة",
                "تصدير التقارير بصيغة PDF / Word"
            ),
            featuresEn = listOf(
                "Unlimited words & checks",
                "Deep syntactic parsing & Harakat diacritics",
                "Optical Character Recognition (OCR) for images",
                "Text-to-speech audio generation",
                "PDF & Word export"
            ),
            isCurrent = false,
            isPopular = true
        ),
        PricingPlan(
            id = "enterprise",
            titleAr = "خطة المؤسسات (Enterprise)",
            titleEn = "Enterprise Plan",
            priceAr = "$29 / شهرياً",
            priceEn = "$29 / month",
            descriptionAr = "حلول متكاملة لدور النشر، المدارس، والجامعات مع دعم فني مخصص.",
            descriptionEn = "Comprehensive solutions for publishers, schools, and teams.",
            featuresAr = listOf(
                "جميع مميزات الخطة الاحترافية",
                "واجهة برمجة التطبيقات (API) للدمج في الأنظمة",
                "تراخيص متعددة لفرق العمل",
                "دعم فني واستشاري مخصص على مدار الساعة"
            ),
            featuresEn = listOf(
                "All Pro Plan features included",
                "Full REST API integration access",
                "Multi-seat team licenses",
                "24/7 dedicated support"
            ),
            isCurrent = false,
            isPopular = false
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("pricing_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isAr) "خطط وأسعار NahwiFix" else "NahwiFix Pricing Plans",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isAr) "اختر الخطة التي تلائم احتياجاتك في التدقيق اللغوي والترقيم العربي"
                    else "Choose the plan that suits your Arabic editing and publishing needs",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        items(plans) { plan ->
            val isPro = plan.isPopular
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .then(
                        if (isPro) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                        else Modifier
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPro) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isPro) 4.dp else 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) plan.titleAr else plan.titleEn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (isPro) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isAr) "الأكثر طلباً" else "Most Popular",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isAr) plan.priceAr else plan.priceEn,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isAr) plan.descriptionAr else plan.descriptionEn,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val features = if (isAr) plan.featuresAr else plan.featuresEn
                    features.forEach { feat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = feat,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (plan.isCurrent) {
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false
                        ) {
                            Text(if (isAr) "خطتك الحالية" else "Current Plan")
                        }
                    } else {
                        Button(
                            onClick = {
                                Toast.makeText(context, if (isAr) "تم اختيار ${plan.titleAr}" else "Selected ${plan.titleEn}", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPro) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text(if (isAr) "ترقية الآن" else "Upgrade Now")
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
