package com.example.talib.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.talib.data.local.ScheduleItem
import com.example.talib.ui.viewmodel.TalibViewModel

@Composable
fun ScheduleScreen(
  viewModel: TalibViewModel
) {
  val scheduleItems by viewModel.currentSchedule.collectAsStateWithLifecycle()
  val selectedDay by viewModel.selectedScheduleDay.collectAsStateWithLifecycle()
  val profile by viewModel.studentProfile.collectAsStateWithLifecycle()

  var scheduleDisplayMode by remember { mutableStateOf("جدول الحصص") } // "جدول الحصص" or "صورة الجدول"
  var reportDialogItem by remember { mutableStateOf<ScheduleItem?>(null) }
  var reportReasonText by remember { mutableStateOf("") }
  var reportSuccessMessage by remember { mutableStateOf<String?>(null) }

  // Report Issue Dialog
  if (reportDialogItem != null) {
    val item = reportDialogItem!!
    AlertDialog(
      onDismissRequest = { reportDialogItem = null },
      title = { Text("تقديم تبليغ عن حصة بالجدول 🚩", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("المادة: ${item.moduleName} (${item.type})", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
          Text("أدخل تفاصيل التغيير أو الخطأ في التوقيت / القاعة لإشعار المشرف:")
          OutlinedTextField(
            value = reportReasonText,
            onValueChange = { reportReasonText = it },
            label = { Text("الملاحظة / الخطأ في التوقيت") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (reportReasonText.isNotBlank()) {
              viewModel.reportIssue("جدول الحصص", "${item.moduleName} - ${item.room}", reportReasonText.trim())
              reportSuccessMessage = "تم إرسال تبليغك للممثل والمشرف لمراجعة التوقيت 🚩"
              reportReasonText = ""
              reportDialogItem = null
            }
          },
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("إرسال التبليغ")
        }
      },
      dismissButton = {
        TextButton(onClick = { reportDialogItem = null }) { Text("إلغاء") }
      }
    )
  }

  val days = listOf(
    Pair(1, "الأحد"),
    Pair(2, "الإثنين"),
    Pair(3, "الثلاثاء"),
    Pair(4, "الأربعاء"),
    Pair(5, "الخميس")
  )

  val dayClasses = scheduleItems.filter { it.dayOfWeek == selectedDay }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("schedule_screen"),
    contentPadding = PaddingValues(bottom = 90.dp, top = 8.dp, start = 16.dp, end = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Cohort & Specialty Header
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "الجدول الأسبوعي للحصص",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
            )
            Text(
              text = "${profile?.specialtyName ?: "الأدب العربي"} • ${profile?.groupNumber?.ifBlank { "بلا فوج (تخصص كامل)" } ?: "بلا فوج"}",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
            )
          }

          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.CalendarMonth,
              contentDescription = null,
              tint = Color.White
            )
          }
        }
      }
    }

    // 2. View Mode Toggle (Manual vs Official Image)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChip(
          selected = scheduleDisplayMode == "جدول الحصص",
          onClick = { scheduleDisplayMode = "جدول الحصص" },
          label = { Text("حصص تفاعلية") },
          leadingIcon = { Icon(Icons.Default.FormatListBulleted, contentDescription = null, modifier = Modifier.size(16.dp)) },
          modifier = Modifier.weight(1f)
        )
        FilterChip(
          selected = scheduleDisplayMode == "صورة الجدول",
          onClick = { scheduleDisplayMode = "صورة الجدول" },
          label = { Text("صورة الجدول الرسمية") },
          leadingIcon = { Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp)) },
          modifier = Modifier.weight(1f)
        )
      }
    }

    if (reportSuccessMessage != null) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
          Text(
            text = reportSuccessMessage ?: "",
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
    }

    if (scheduleDisplayMode == "صورة الجدول") {
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Icon(
              Icons.Default.PhotoLibrary,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(54.dp)
            )
            Text(
              text = "جدول التوقيت الرسمي الصادر عن الكلية",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.fillMaxWidth().height(180.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = "📄 المخطط الزمني الشامل معتمد ومتاح للتحميل",
                  style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
              }
            }
          }
        }
      }
    } else {
      // 3. Day Selector Row
      item {
        LazyRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(days) { (dayNum, dayName) ->
            val isSelected = dayNum == selectedDay
            FilterChip(
              selected = isSelected,
              onClick = { viewModel.selectScheduleDay(dayNum) },
              label = {
                Text(
                  text = dayName,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = Color.White
              ),
              shape = RoundedCornerShape(14.dp)
            )
          }
        }
      }

      // 4. Classes for selected Day
      item {
        Text(
          text = "حصص يوم ${days.find { it.first == selectedDay }?.second ?: ""}",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      }

      if (dayClasses.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.EventBusy,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(44.dp)
              )
              Text(
                text = "لا توجد حصص مبرمجة في هذا اليوم",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "يوم راحة أو مراجعة ذاتية في المكتبة الجامعية.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )
            }
          }
        }
      } else {
        items(dayClasses, key = { it.id }) { item ->
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              // Time Column Badge
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                  .clip(RoundedCornerShape(14.dp))
                  .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                  .padding(horizontal = 10.dp, vertical = 8.dp)
              ) {
                Text(
                  text = item.startTime,
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                  )
                )
                Text(
                  text = "إلى",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                )
                Text(
                  text = item.endTime,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  )
                )
              }

              // Info Column
              Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(
                        when {
                          item.type.contains("محاضرة") -> MaterialTheme.colorScheme.primary
                          item.type.contains("TD") -> Color(0xFF10B981)
                          else -> Color(0xFF3B82F6)
                        }
                      )
                      .padding(horizontal = 8.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = item.type,
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                      )
                    )
                  }

                  IconButton(
                    onClick = { reportDialogItem = item },
                    modifier = Modifier.size(24.dp)
                  ) {
                    Icon(
                      Icons.Default.Flag,
                      contentDescription = "تبليغ",
                      tint = MaterialTheme.colorScheme.error,
                      modifier = Modifier.size(15.dp)
                    )
                  }
                }

                Text(
                  text = item.moduleName,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp)
                )

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                  )
                  Text(
                    text = item.room,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                  )
                }

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                  )
                  Text(
                    text = item.professor,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
