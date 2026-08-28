package com.example.talib.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.talib.ui.viewmodel.TalibViewModel

@Composable
fun OnboardingScreen(
  viewModel: TalibViewModel,
  onComplete: () -> Unit
) {
  var step by remember { mutableStateOf(1) } // 1 to 5

  var selectedInstitution by remember { mutableStateOf("المدرسة العليا للأساتذة - بوزريعة (ENS)") }
  var selectedSpecialty by remember { mutableStateOf("اللغة والأدب العربي") }
  var selectedTrack by remember { mutableStateOf("أستاذ التعليم الابتدائي") }
  var selectedYear by remember { mutableStateOf("السنة الثانية (L2)") }
  var selectedSemester by remember { mutableStateOf("السداسي الأول (S1)") }
  var selectedGroup by remember { mutableStateOf("الفوج 03") }
  var studentFullName by remember { mutableStateOf("محمد البشير بن علي") }

  val institutions = listOf(
    "المدرسة العليا للأساتذة - بوزريعة (ENS)",
    "المدرسة العليا للأساتذة - القبة (ENS)",
    "جامعة الجزائر 1 - بن يوسف بن خدة",
    "جامعة الجزائر 2 - أبو القاسم سعد الله",
    "جامعة الجزائر 3 - إبراهيم سلطان شيبوط",
    "جامعة العلوم والتكنولوجيا - هواري بومدين (USTHB)",
    "جامعة قسنطينة 1 - الإخوة منتوري",
    "جامعة وهران 1 - أحمد بن بلة",
    "جامعة سطيف 1 - فرحات عباس"
  )

  val specialtiesList = mapOf(
    "المدرسة العليا للأساتذة - بوزريعة (ENS)" to listOf(
      "اللغة والأدب العربي",
      "اللغة الإنجليزية",
      "اللغة الفرنسية",
      "التاريخ والجغرافيا",
      "الفلسفة"
    ),
    "جامعة العلوم والتكنولوجيا - هواري بومدين (USTHB)" to listOf(
      "الإعلام الآلي وتطوير البرمجيات",
      "الذكاء الاصطناعي وعلوم البيانات",
      "الرياضيات التطبيقية",
      "الإلكترونيك والاتصالات"
    )
  )

  val tracksList = mapOf(
    "اللغة والأدب العربي" to listOf(
      "أستاذ التعليم الابتدائي",
      "أستاذ التعليم المتوسط",
      "أستاذ التعليم الثانوي",
      "دراسات لغوية ولسانيات",
      "دراسات أدبية ونقدية"
    ),
    "اللغة الإنجليزية" to listOf(
      "أستاذ التعليم المتوسط",
      "أستاذ التعليم الثانوي",
      "أدب وحضارة إنجليزية"
    ),
    "الإعلام الآلي وتطوير البرمجيات" to listOf(
      "هندسة البرمجيات ونظم المعلومات",
      "نظم وشبكات موزعة",
      "الذكاء الاصطناعي"
    )
  )

  val years = listOf("السنة الأولى (L1)", "السنة الثانية (L2)", "السنة الثالثة (L3)", "ماستر 1 (M1)", "ماستر 2 (M2)")
  val semesters = listOf("السداسي الأول (S1)", "السداسي الثاني (S2)")
  val groups = listOf("الفوج 01", "الفوج 02", "الفوج 03", "الفوج 04", "الفوج 05", "الفوج 06")

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .testTag("onboarding_screen"),
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface)
          .padding(horizontal = 20.dp, vertical = 16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Column {
              Text(
                text = "طالب | مسارك الأكاديمي",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
              )
              Text(
                text = "تهيئة البيئة الدراسية المخصصة",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )
            }
          }

          Text(
            text = "الخطوة $step من 4",
            style = MaterialTheme.typography.labelMedium.copy(
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Bold
            )
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
          progress = { step / 4f },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = MaterialTheme.colorScheme.primary,
          trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        )
      }
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 20.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
      // Step 1: المؤسسة الجامعية
      if (step == 1) {
        item {
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Icon(Icons.Default.AccountBalance, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Text(
                text = "اختر مؤسستك الجامعية أو مدرستك العليا لضبط المواد والتقويم الأكاديمي المعتمد.",
                style = MaterialTheme.typography.bodyMedium
              )
            }
          }
        }

        item {
          Text(
            text = "اختر المؤسسة الجامعية:",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
        }

        items(institutions.size) { index ->
          val inst = institutions[index]
          val isSelected = inst == selectedInstitution
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
              else MaterialTheme.colorScheme.surface
            ),
            border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
              brush = Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary))
            ) else null,
            onClick = { selectedInstitution = inst },
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                RadioButton(
                  selected = isSelected,
                  onClick = { selectedInstitution = inst }
                )
                Text(
                  text = inst,
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                )
              }
            }
          }
        }
      }

      // Step 2: التخصص والملمح الدراسي
      if (step == 2) {
        item {
          Text(
            text = "التخصص والملمح التكويني في ${selectedInstitution.substringBefore(" (")}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
          )
        }

        val specs = specialtiesList[selectedInstitution] ?: listOf(
          "اللغة والأدب العربي",
          "الإعلام الآلي وتطوير البرمجيات",
          "العلوم الاقتصادية والتجارية",
          "الحقوق والعلوم السياسية"
        )

        item {
          Text("اختر التخصص:", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
        }

        items(specs.size) { index ->
          val spec = specs[index]
          val isSelected = spec == selectedSpecialty
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
              else MaterialTheme.colorScheme.surface
            ),
            onClick = {
              selectedSpecialty = spec
              selectedTrack = tracksList[spec]?.firstOrNull() ?: "المسار الأكاديمي العام"
            },
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              RadioButton(selected = isSelected, onClick = {
                selectedSpecialty = spec
                selectedTrack = tracksList[spec]?.firstOrNull() ?: "المسار الأكاديمي العام"
              })
              Text(spec, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(10.dp))
          Text("اختر الملمح / الشعبة (Track):", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
        }

        val tracks = tracksList[selectedSpecialty] ?: listOf(
          "أستاذ التعليم الابتدائي",
          "أستاذ التعليم المتوسط",
          "أستاذ التعليم الثانوي",
          "المسار الأكاديمي العام"
        )

        items(tracks.size) { index ->
          val track = tracks[index]
          val isSelected = track == selectedTrack
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
              else MaterialTheme.colorScheme.surface
            ),
            onClick = { selectedTrack = track },
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              RadioButton(selected = isSelected, onClick = { selectedTrack = track })
              Text(track, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
            }
          }
        }
      }

      // Step 3: السنة الدراسية، السداسي، والفوج
      if (step == 3) {
        item {
          Text(
            text = "المستوى الدراسي والفوج",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
          )
        }

        item {
          Text("السنة الدراسية:", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            years.take(3).forEach { yr ->
              val isSelected = yr == selectedYear
              FilterChip(
                selected = isSelected,
                onClick = { selectedYear = yr },
                label = { Text(yr) }
              )
            }
          }
        }

        item {
          Text("السداسي (Semester):", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            semesters.forEach { sem ->
              val isSelected = sem == selectedSemester
              FilterChip(
                selected = isSelected,
                onClick = { selectedSemester = sem },
                label = { Text(sem) }
              )
            }
          }
        }

        item {
          Text("فوجك الدراسي المخصص:", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
          Spacer(modifier = Modifier.height(6.dp))
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            groups.forEach { grp ->
              val isSelected = grp == selectedGroup
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                ),
                onClick = { selectedGroup = grp },
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  RadioButton(selected = isSelected, onClick = { selectedGroup = grp })
                  Text(grp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                }
              }
            }
          }
        }
      }

      // Step 4: تأكيد البيانات والاسم
      if (step == 4) {
        item {
          Text(
            text = "تأكيد مسارك الأكاديمي المغلق",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
          )
        }

        item {
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(20.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Text(
                text = "ملخص عالمك الأكاديمي",
                style = MaterialTheme.typography.labelLarge.copy(color = Color.White.copy(alpha = 0.8f))
              )

              Text(
                text = "$selectedSpecialty • $selectedTrack",
                style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Black)
              )

              HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text("المؤسسة", style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f)))
                  Text(selectedInstitution, style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                }
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text("المستوى والفوج", style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f)))
                  Text("$selectedYear • $selectedSemester • $selectedGroup", style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                }
              }
            }
          }
        }

        item {
          OutlinedTextField(
            value = studentFullName,
            onValueChange = { studentFullName = it },
            label = { Text("الاسم واللقب") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          )
        }

        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Text(
                text = "تجربتك ستكون مغلقة بالكامل على هذا الفوج والتخصص دون أي تشتيت. يمكنك تغيير المسار لاحقاً من الإعدادات.",
                style = MaterialTheme.typography.bodySmall
              )
            }
          }
        }
      }

      // Bottom Navigation Buttons
      item {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          if (step > 1) {
            OutlinedButton(
              onClick = { step-- },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("السابق")
            }
          }

          Button(
            onClick = {
              if (step < 4) {
                step++
              } else {
                // Complete onboarding
                viewModel.updateProfile(
                  com.example.talib.data.local.StudentProfile(
                    fullName = studentFullName,
                    institution = selectedInstitution,
                    university = selectedInstitution,
                    specialtyName = selectedSpecialty,
                    profileTrack = selectedTrack,
                    academicYearName = selectedYear,
                    semesterName = selectedSemester,
                    groupNumber = selectedGroup,
                    isConfigured = true
                  )
                )
                onComplete()
              }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(if (step == 1) 1f else 1.5f)
          ) {
            Text(
              text = if (step == 4) "دخول عالمي الدراسي 🎓" else "متابعة",
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
          }
        }
      }
    }
  }
}
