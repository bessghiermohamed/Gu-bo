package com.example.talib.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.talib.data.local.StudentGrade
import com.example.talib.ui.viewmodel.TalibViewModel

@Composable
fun GradesScreen(
  viewModel: TalibViewModel
) {
  val grades by viewModel.allGrades.collectAsStateWithLifecycle()
  val gpa by viewModel.calculatedGPA.collectAsStateWithLifecycle()

  var editingGrade by remember { mutableStateOf<StudentGrade?>(null) }

  // Edit Grade Dialog
  editingGrade?.let { grade ->
    var tdText by remember { mutableStateOf(grade.continuousScore.toString()) }
    var examText by remember { mutableStateOf(grade.examScore.toString()) }

    AlertDialog(
      onDismissRequest = { editingGrade = null },
      title = { Text("تعديل علامة: ${grade.moduleName}", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          OutlinedTextField(
            value = tdText,
            onValueChange = { tdText = it },
            label = { Text("علامة المراقبة المستمرة / TD (من 20)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = examText,
            onValueChange = { examText = it },
            label = { Text("علامة الامتحان النهائي (من 20)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val tdVal = tdText.toDoubleOrNull() ?: grade.continuousScore
            val exVal = examText.toDoubleOrNull() ?: grade.examScore
            viewModel.updateGradeScore(grade, tdVal, exVal)
            editingGrade = null
          },
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("حفظ العلامات")
        }
      },
      dismissButton = {
        TextButton(onClick = { editingGrade = null }) {
          Text("إلغاء")
        }
      }
    )
  }

  val totalCredits = grades.filter {
    val avg = (it.continuousScore * 0.4) + (it.examScore * 0.6)
    avg >= 10.0
  }.sumOf { it.credits }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("grades_screen"),
    contentPadding = PaddingValues(bottom = 90.dp, top = 8.dp, start = 16.dp, end = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. GPA Master Card
    item {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (gpa >= 10.0) MaterialTheme.colorScheme.primaryContainer else Color(0xFFFEE2E2)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = "المعدل الفصلي العام (السداسي 1)",
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              color = if (gpa >= 10.0) MaterialTheme.colorScheme.primary else Color(0xFFDC2626)
            )
          )

          Text(
            text = String.format("%.2f", gpa),
            style = MaterialTheme.typography.displayLarge.copy(
              fontWeight = FontWeight.Black,
              fontSize = 44.sp,
              color = if (gpa >= 10.0) MaterialTheme.colorScheme.primary else Color(0xFFDC2626)
            )
          )

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(
                when {
                  gpa >= 14.0 -> Color(0xFF10B981)
                  gpa >= 10.0 -> MaterialTheme.colorScheme.primary
                  else -> Color(0xFFEF4444)
                }
              )
              .padding(horizontal = 14.dp, vertical = 6.dp)
          ) {
            Text(
              text = when {
                gpa >= 16.0 -> "تقدير: ممتاز (Très Bien) 🌟"
                gpa >= 14.0 -> "تقدير: جيد (Bien) ✨"
                gpa >= 12.0 -> "تقدير: قريب من الجيد (Assez Bien) 👍"
                gpa >= 10.0 -> "ناجح (Admis) ✓"
                else -> "غير مستوفٍ (Ajourné) - دورة الاستدراك"
              },
              style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
            )
          }

          HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "مجموع الأرصدة المكتسبة", style = MaterialTheme.typography.bodySmall)
              Text(
                text = "$totalCredits / 30 رصيد",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black)
              )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "عدد المقاييس الموفقة", style = MaterialTheme.typography.bodySmall)
              Text(
                text = "${grades.count { ((it.continuousScore * 0.4) + (it.examScore * 0.6)) >= 10.0 }} / ${grades.size}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black)
              )
            }
          }
        }
      }
    }

    // 2. Module Grades Breakdown
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "كشف نقاط المقاييس (اضغط للتعديل)",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      }
    }

    items(grades, key = { it.id }) { grade ->
      val moduleAvg = (grade.continuousScore * 0.4) + (grade.examScore * 0.6)
      val isPassed = moduleAvg >= 10.0

      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = grade.moduleName,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, fontSize = 15.sp),
              modifier = Modifier.weight(1f)
            )

            IconButton(
              onClick = { editingGrade = grade },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "تعديل العلامة",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "TD/TP: ${grade.continuousScore}  •  امتحان: ${grade.examScore}  •  معامل: ${grade.coefficient}",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isPassed) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = String.format("%.2f / 20", moduleAvg),
                style = MaterialTheme.typography.labelMedium.copy(
                  color = if (isPassed) Color(0xFF059669) else Color(0xFFDC2626),
                  fontWeight = FontWeight.Black
                )
              )
            }
          }
        }
      }
    }
  }
}
