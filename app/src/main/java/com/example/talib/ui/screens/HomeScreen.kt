package com.example.talib.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.talib.ui.components.GridActionCard
import com.example.talib.ui.viewmodel.ScreenRoute
import com.example.talib.ui.viewmodel.TalibViewModel

@Composable
fun HomeScreen(
  viewModel: TalibViewModel,
  onNavigate: (ScreenRoute) -> Unit
) {
  val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
  val profile by viewModel.studentProfile.collectAsStateWithLifecycle()
  val gpa by viewModel.calculatedGPA.collectAsStateWithLifecycle()
  val announcements by viewModel.allAnnouncements.collectAsStateWithLifecycle()
  val schedule by viewModel.currentSchedule.collectAsStateWithLifecycle()
  val exams by viewModel.allExams.collectAsStateWithLifecycle()
  val modules by viewModel.currentModules.collectAsStateWithLifecycle()
  val polls by viewModel.allPolls.collectAsStateWithLifecycle()
  val libraryReferences by viewModel.allLibraryReferences.collectAsStateWithLifecycle()
  val attendanceRecords by viewModel.allAttendanceRecords.collectAsStateWithLifecycle()
  val allAcademicGroups by viewModel.allAcademicGroups.collectAsStateWithLifecycle()
  val allCohortGroups by viewModel.allCohortGroups.collectAsStateWithLifecycle()
  val myJoinRequests by viewModel.myJoinRequests.collectAsStateWithLifecycle()

  var showJoinGroupDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

  val hasNoCohort = profile?.groupNumber.isNullOrBlank() || profile?.groupNumber == "بلا فوج"
  val pendingMyRequest = myJoinRequests.firstOrNull { it.status == "PENDING" }

  if (showJoinGroupDialog && profile != null) {
    var selectedGrpId by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(allAcademicGroups.firstOrNull()?.id ?: 1L) }
    val cohortsInGroup = androidx.compose.runtime.remember(selectedGrpId, allCohortGroups) {
      allCohortGroups.filter { it.academicGroupId == selectedGrpId }.ifEmpty { allCohortGroups }
    }
    var selectedCohortId by androidx.compose.runtime.remember(cohortsInGroup) {
      androidx.compose.runtime.mutableStateOf(cohortsInGroup.firstOrNull()?.id ?: 1L)
    }

    AlertDialog(
      onDismissRequest = { showJoinGroupDialog = false },
      title = { Text("طلب الانضمام إلى فوج دراسي 👥", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "اختر المجموعة والفوج الدراسي الذي تنتمي إليه لإرسال طلب اعتماد للممثل:",
            style = MaterialTheme.typography.bodyMedium
          )
          Text("1. اختر المجموعة الأكاديمية:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            allAcademicGroups.forEach { grp ->
              FilterChip(
                selected = selectedGrpId == grp.id,
                onClick = { selectedGrpId = grp.id },
                label = { Text(grp.groupName, fontSize = 11.sp) }
              )
            }
          }

          Text("2. اختر الفوج الفرعي:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            cohortsInGroup.forEach { cohort ->
              FilterChip(
                selected = selectedCohortId == cohort.id,
                onClick = { selectedCohortId = cohort.id },
                label = { Text(cohort.groupName, fontSize = 11.sp) }
              )
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val grp = allAcademicGroups.find { it.id == selectedGrpId }
            val coh = allCohortGroups.find { it.id == selectedCohortId }
            val gName = grp?.groupName ?: "المجموعة 01"
            val cName = coh?.groupName ?: "الفوج 01"
            viewModel.submitGroupJoinRequest(
              groupId = selectedGrpId,
              cohortId = selectedCohortId,
              groupName = gName,
              cohortName = cName
            )
            showJoinGroupDialog = false
          },
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("إرسال طلب الانضمام")
        }
      },
      dismissButton = {
        TextButton(onClick = { showJoinGroupDialog = false }) { Text("إلغاء") }
      }
    )
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("home_screen"),
    contentPadding = PaddingValues(bottom = 90.dp, top = 8.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Cohort Join Request / Assignment Banner for Students
    if (hasNoCohort) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (pendingMyRequest != null) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else Color(0xFFFFFBEB)
          ),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (pendingMyRequest != null) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            else Color(0xFFFDE68A)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                  if (pendingMyRequest != null) MaterialTheme.colorScheme.primary
                  else Color(0xFFD97706)
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (pendingMyRequest != null) Icons.Default.HourglassTop else Icons.Default.GroupAdd,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
            Column(modifier = Modifier.weight(1f)) {
              if (pendingMyRequest != null) {
                Text(
                  text = "طلب الانضمام قيد المراجعة ⏳",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "تم إرسال طلبك لـ [${pendingMyRequest.groupName} • ${pendingMyRequest.cohortName}] بانتظار اعتماد الممثل أو المشرف.",
                  style = MaterialTheme.typography.bodySmall
                )
              } else {
                Text(
                  text = "أنت غير ملحق بأي فوج دراسي بعد",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                )
                Text(
                  text = "أرسل طلب انضمام لفوجك أو انتظر إلحاقك مباشرة من قِبل ممثل الفوج.",
                  style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB45309))
                )
              }
            }
            if (pendingMyRequest == null) {
              Button(
                onClick = { showJoinGroupDialog = true },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
              ) {
                Text("طلب انضمام", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
    // 1. Hero Card with Student Greeting & Closed Academic Info
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .height(180.dp)
          .shadow(12.dp, RoundedCornerShape(24.dp))
          .clip(RoundedCornerShape(24.dp))
      ) {
        Image(
          painter = painterResource(id = R.drawable.talib_hero_banner_1787593996541),
          contentDescription = "بانر طالب",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay for contrast
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color.Black.copy(alpha = 0.45f),
                  Color.Black.copy(alpha = 0.85f)
                )
              )
            )
        )

        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
          verticalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "مرحباً، ${profile?.fullName ?: "طالب العلم"}",
                style = MaterialTheme.typography.titleLarge.copy(
                  color = Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 18.sp
                )
              )
              Text(
                text = "${profile?.institution ?: "المدرسة العليا للأساتذة"} • ${profile?.profileTrack ?: profile?.specialtyName ?: "الأدب العربي"}",
                style = MaterialTheme.typography.bodyMedium.copy(
                  color = Color.White.copy(alpha = 0.85f),
                  fontWeight = FontWeight.Medium
                )
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = profile?.groupNumber ?: "الفوج 03",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Color.White,
                  fontWeight = FontWeight.Bold
                )
              )
            }
          }

          // Stats Quick Bar
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(Color.White.copy(alpha = 0.15f))
              .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Equalizer,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = "المعدل التقديري:",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
              )
              Text(
                text = if (gpa > 0.0) "$gpa / 20" else "-- / 20",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color.White,
                  fontWeight = FontWeight.Bold
                )
              )
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Book,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = "${modules.size} مقاييس",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color.White,
                  fontWeight = FontWeight.Bold
                )
              )
            }
          }
        }
      }
    }

    // 2. Main Quick Action Grid
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "الخدمات والمساحة الأكاديمية",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
        )

        // Row 1: المقررات + المحاضرات والملفات
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          GridActionCard(
            title = "المقررات",
            icon = Icons.Default.Book,
            badgeText = "${modules.size} مقاييس",
            isDarkMode = isDarkMode,
            delayOffsetMs = 100,
            onClick = { onNavigate(ScreenRoute.COURSES) },
            modifier = Modifier.weight(1f)
          )

          GridActionCard(
            title = "المحاضرات",
            icon = Icons.Default.Description,
            badgeText = "قراءة وتحميل",
            isDarkMode = isDarkMode,
            delayOffsetMs = 200,
            onClick = { onNavigate(ScreenRoute.LECTURES) },
            modifier = Modifier.weight(1f)
          )
        }

        // Row 2: الجدول + الاختبارات
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          GridActionCard(
            title = "الجدول",
            icon = Icons.Default.CalendarMonth,
            badgeText = "التوقيت والقاعات",
            isDarkMode = isDarkMode,
            delayOffsetMs = 300,
            onClick = { onNavigate(ScreenRoute.SCHEDULE) },
            modifier = Modifier.weight(1f)
          )

          GridActionCard(
            title = "الاختبارات",
            icon = Icons.Default.Science,
            badgeText = "${exams.size} اختبارات",
            isDarkMode = isDarkMode,
            delayOffsetMs = 400,
            onClick = { onNavigate(ScreenRoute.EXAMS) },
            modifier = Modifier.weight(1f)
          )
        }

        // Row 3: الواجبات + حاسبة الطالب
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          GridActionCard(
            title = "الواجبات",
            icon = Icons.Default.EditNote,
            badgeText = "تسليمات ومتابعة",
            isDarkMode = isDarkMode,
            delayOffsetMs = 500,
            onClick = { onNavigate(ScreenRoute.ASSIGNMENTS) },
            modifier = Modifier.weight(1f)
          )

          GridActionCard(
            title = "حاسبة الطالب",
            icon = Icons.Default.Equalizer,
            badgeText = if (gpa > 0.0) "$gpa / 20" else "حساب مباشر",
            isDarkMode = isDarkMode,
            delayOffsetMs = 600,
            onClick = { onNavigate(ScreenRoute.GRADES) },
            modifier = Modifier.weight(1f)
          )
        }

        // Row 4: ملفاتي + المحتوى المحفوظ أوفلاين
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          GridActionCard(
            title = "ملفاتي",
            icon = Icons.Default.Folder,
            badgeText = "دفتر الطالب",
            isDarkMode = isDarkMode,
            delayOffsetMs = 700,
            onClick = { onNavigate(ScreenRoute.MY_FILES) },
            modifier = Modifier.weight(1f)
          )

          GridActionCard(
            title = "المحتوى المحفوظ",
            icon = Icons.Default.CloudDone,
            badgeText = "بدون إنترنت",
            isDarkMode = isDarkMode,
            delayOffsetMs = 800,
            onClick = { onNavigate(ScreenRoute.OFFLINE_CACHE) },
            modifier = Modifier.weight(1f)
          )
        }

        // Row 5: الإعلانات + الفوج
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          GridActionCard(
            title = "الإعلانات",
            icon = Icons.Default.Campaign,
            badgeText = "${announcements.size} تنبيهات",
            isDarkMode = isDarkMode,
            delayOffsetMs = 900,
            onClick = { onNavigate(ScreenRoute.ANNOUNCEMENTS) },
            modifier = Modifier.weight(1f)
          )

          GridActionCard(
            title = "الفوج",
            icon = Icons.Default.Groups,
            badgeText = profile?.groupNumber ?: "الفوج 03",
            isDarkMode = isDarkMode,
            delayOffsetMs = 1000,
            onClick = { onNavigate(ScreenRoute.GROUP) },
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 4. Upcoming Schedule Section
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "برنامج الحصص القادمة",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
          )
          TextButton(onClick = { onNavigate(ScreenRoute.SCHEDULE) }) {
            Text("عرض الجدول كاملاً")
          }
        }

        if (schedule.isEmpty()) {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "لا توجد حصص مجدولة حالياً لهذا اليوم.",
              modifier = Modifier.padding(16.dp),
              style = MaterialTheme.typography.bodyMedium
            )
          }
        } else {
          schedule.take(2).forEach { item ->
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (item.type.contains("محاضرة")) Icons.Default.School else Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                  )
                }

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = item.moduleName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                  )
                  Text(
                    text = "${item.type} • ${item.room} • ${item.professor}",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = item.startTime,
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = MaterialTheme.colorScheme.primary,
                      fontWeight = FontWeight.Bold
                    )
                  )
                }
              }
            }
          }
        }
      }
    }

    // 5. Latest Announcements
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "آخر الإعلانات البيداغوجية",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
          )
          TextButton(onClick = { onNavigate(ScreenRoute.ANNOUNCEMENTS) }) {
            Text("كل الإعلانات")
          }
        }

        if (announcements.isEmpty()) {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
          ) {
            Row(
              modifier = Modifier.padding(16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Icon(Icons.Default.Campaign, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Text(
                text = "لا توجد إعلانات منشورة حالياً.",
                style = MaterialTheme.typography.bodySmall
              )
            }
          }
        } else {
          LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            items(announcements.take(3)) { ann ->
              Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                modifier = Modifier
                  .width(260.dp)
                  .clickable { onNavigate(ScreenRoute.ANNOUNCEMENTS) }
              ) {
                Column(
                  modifier = Modifier.padding(14.dp),
                  verticalArrangement = Arrangement.spacedBy(6.dp)
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
                          when (ann.urgency) {
                            "عاجل" -> Color(0xFFEF4444)
                            "هام" -> Color(0xFFF59E0B)
                            else -> MaterialTheme.colorScheme.primary
                          }
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = ann.urgency,
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                      )
                    }

                    Text(
                      text = ann.date,
                      style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                  }

                  Text(
                    text = ann.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                  )

                  Text(
                    text = ann.content,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
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
