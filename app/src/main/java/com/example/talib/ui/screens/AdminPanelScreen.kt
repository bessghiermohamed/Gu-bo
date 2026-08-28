package com.example.talib.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.talib.data.local.AppUser
import com.example.talib.ui.viewmodel.ScreenRoute
import com.example.talib.ui.viewmodel.TalibViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
  viewModel: TalibViewModel,
  onNavigate: (ScreenRoute) -> Unit
) {
  val modules by viewModel.allModules.collectAsStateWithLifecycle()
  val specialties by viewModel.specialties.collectAsStateWithLifecycle()
  val profile by viewModel.studentProfile.collectAsStateWithLifecycle()
  val users by viewModel.allUsers.collectAsStateWithLifecycle()
  val issueReports by viewModel.allIssueReports.collectAsStateWithLifecycle()

  val currentRole = profile?.userRole ?: "OWNER"

  var selectedTab by remember { mutableStateOf(0) } // 0: رفع المحتوى الموحد, 1: إدارة المستخدمين والرتب, 2: التبليغات والشكاوى

  var showUploadDialog by remember { mutableStateOf(false) }
  var uploadContentType by remember { mutableStateOf("محاضرة") } // محاضرة / إعلان / حصة جدول / امتحان
  var visibilityScope by remember { mutableStateOf("تخصص كامل") } // تخصص كامل / عدة أفواج محددة / فوج واحد
  var targetGroupText by remember { mutableStateOf("الكل") }

  var statusMessage by remember { mutableStateOf<String?>(null) }
  var showAddStudentDialog by remember { mutableStateOf(false) }

  // Upload Content Dialog with mandatory Visibility Scope
  if (showUploadDialog) {
    var selectedModId by remember { mutableStateOf(modules.firstOrNull()?.id ?: 1L) }
    var titleText by remember { mutableStateOf("") }
    var contentText by remember { mutableStateOf("") }
    var extraDetailText by remember { mutableStateOf("") }
    var urgencyText by remember { mutableStateOf("عام") }
    var modExpanded by remember { mutableStateOf(false) }

    AlertDialog(
      onDismissRequest = { showUploadDialog = false },
      title = {
        Text("رفع ونشر: $uploadContentType", fontWeight = FontWeight.Black)
      },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Mandatory Visibility Scope selector
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
          ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text("من يرى هذا المحتوى؟ (نطاق الظهور الإلزامي):", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("تخصص كامل", "عدة أفواج محددة", "فوج واحد").forEach { sc ->
                  FilterChip(
                    selected = visibilityScope == sc,
                    onClick = {
                      visibilityScope = sc
                      targetGroupText = if (sc == "تخصص كامل") "الكل" else if (sc == "فوج واحد") (profile?.groupNumber ?: "الفوج 03") else "الأفواج 01، 02، 03"
                    },
                    label = { Text(sc, fontSize = 10.sp) }
                  )
                }
              }
              if (visibilityScope != "تخصص كامل") {
                OutlinedTextField(
                  value = targetGroupText,
                  onValueChange = { targetGroupText = it },
                  label = { Text("الأفواج المستهدفة") },
                  modifier = Modifier.fillMaxWidth()
                )
              }
            }
          }

          if (uploadContentType == "محاضرة" || uploadContentType == "امتحان") {
            ExposedDropdownMenuBox(
              expanded = modExpanded,
              onExpandedChange = { modExpanded = !modExpanded }
            ) {
              OutlinedTextField(
                value = modules.find { it.id == selectedModId }?.name ?: "اختر المقياس",
                onValueChange = {},
                readOnly = true,
                label = { Text("المقياس الدراسي") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
              )
              ExposedDropdownMenu(
                expanded = modExpanded,
                onDismissRequest = { modExpanded = false }
              ) {
                modules.forEach { mod ->
                  DropdownMenuItem(
                    text = { Text(mod.name) },
                    onClick = {
                      selectedModId = mod.id
                      modExpanded = false
                    }
                  )
                }
              }
            }
          }

          OutlinedTextField(
            value = titleText,
            onValueChange = { titleText = it },
            label = { Text(if (uploadContentType == "إعلان") "عنوان الإعلان" else "العنوان / الموضوع") },
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = contentText,
            onValueChange = { contentText = it },
            label = { Text(if (uploadContentType == "إعلان") "نص الإعلان" else "الملخص / التفاصيل") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
          )

          if (uploadContentType == "محاضرة") {
            OutlinedTextField(
              value = extraDetailText.ifBlank { "lecture_file.pdf" },
              onValueChange = { extraDetailText = it },
              label = { Text("اسم ملف PDF المرفق") },
              modifier = Modifier.fillMaxWidth()
            )
          }

          if (uploadContentType == "إعلان") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              listOf("عام", "هام", "عاجل").forEach { urg ->
                FilterChip(
                  selected = urgencyText == urg,
                  onClick = { urgencyText = urg },
                  label = { Text(urg) }
                )
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (titleText.isNotBlank()) {
              when (uploadContentType) {
                "محاضرة" -> {
                  viewModel.addLecture(
                    moduleId = selectedModId,
                    weekNumber = 3,
                    title = titleText,
                    summary = contentText.ifBlank { "ملخص المحاضرة" },
                    pdfFileName = extraDetailText.ifBlank { "lecture.pdf" },
                    durationMinutes = 90,
                    visibilityScope = visibilityScope,
                    targetGroup = targetGroupText
                  )
                }
                "إعلان" -> {
                  viewModel.publishAnnouncement(
                    title = titleText,
                    content = contentText.ifBlank { "تفاصيل الإعلان" },
                    author = "${profile?.userRole ?: "ممثل"}: ${profile?.fullName ?: "الممثل"}",
                    urgency = urgencyText,
                    visibilityScope = visibilityScope,
                    targetGroups = targetGroupText
                  )
                }
                "امتحان" -> {
                  val mod = modules.find { it.id == selectedModId }
                  viewModel.addExam(
                    moduleId = selectedModId,
                    moduleName = mod?.name ?: "مقياس دراسي",
                    title = titleText,
                    date = "خلال أسبوع الامتحانات",
                    time = "09:00",
                    room = "قاعة 12",
                    coeff = 3.0,
                    visibilityScope = visibilityScope,
                    targetGroup = targetGroupText
                  )
                }
                "حصة جدول" -> {
                  viewModel.addScheduleItem(
                    dayOfWeek = 1,
                    startTime = "08:30",
                    endTime = "10:00",
                    moduleName = titleText,
                    type = "أعمال موجهة TD",
                    room = "قاعة 10",
                    professor = "أستاذ المقياس",
                    visibilityScope = visibilityScope,
                    targetGroup = targetGroupText
                  )
                }
              }
              showUploadDialog = false
              statusMessage = "تم نشر $uploadContentType فوراً بنطاق [$visibilityScope] بنجاح! 🚀"
            }
          },
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("نشر فوري بدون مراجعة")
        }
      },
      dismissButton = {
        TextButton(onClick = { showUploadDialog = false }) { Text("إلغاء") }
      }
    )
  }

  // Add Student Dialog
  if (showAddStudentDialog) {
    var sName by remember { mutableStateOf("") }
    var sEmail by remember { mutableStateOf("") }
    var sId by remember { mutableStateOf("20263108") }
    var sGroup by remember { mutableStateOf(profile?.groupNumber ?: "الفوج 03") }

    AlertDialog(
      onDismissRequest = { showAddStudentDialog = false },
      title = { Text("إضافة طالب جديد للفوج يدوياً", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = sName,
            onValueChange = { sName = it },
            label = { Text("اسم ولقب الطالب") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = sEmail,
            onValueChange = { sEmail = it },
            label = { Text("البريد الإلكتروني الجامعي") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = sGroup,
            onValueChange = { sGroup = it },
            label = { Text("الفوج") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (sName.isNotBlank()) {
              viewModel.addUser(
                fullName = sName,
                email = sEmail.ifBlank { "student@univ.dz" },
                studentId = sId,
                groupNumber = sGroup,
                role = "STUDENT"
              )
              showAddStudentDialog = false
              statusMessage = "تمت إضافة الطالب $sName لقائمة الفوج بنجاح."
            }
          },
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("إضافة الطالب")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddStudentDialog = false }) { Text("إلغاء") }
      }
    )
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .testTag("admin_panel_screen"),
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("لوحة الإدارة والرتب الأكاديمية", fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text(
              text = "رتبتك الحالية: ${
                when (currentRole) {
                  "OWNER" -> "المالك (Super Admin)"
                  "SPECIALTY_ADMIN" -> "مسؤول التخصص"
                  "REPRESENTATIVE" -> "ممثل الفوج / السنة"
                  else -> "ممثل معتمد"
                }
              }",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.primary
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = { onNavigate(ScreenRoute.HOME) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
          }
        }
      )
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp)
    ) {
      // Role Switcher Simulator for testing permissions
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("معاينة الصلاحيات حسب الرتبة:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              listOf("OWNER", "SPECIALTY_ADMIN", "REPRESENTATIVE").forEach { r ->
                FilterChip(
                  selected = (profile?.userRole ?: "OWNER") == r,
                  onClick = { viewModel.switchUserRole(r) },
                  label = {
                    Text(
                      when (r) {
                        "OWNER" -> "👑 المالك"
                        "SPECIALTY_ADMIN" -> "🏛️ مسؤول تخصص"
                        else -> "🎓 ممثل فوج"
                      },
                      fontSize = 11.sp
                    )
                  }
                )
              }
            }
          }
        }
      }

      // Tab selector
      item {
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = MaterialTheme.colorScheme.surface
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = { Text("رفع ونشر المحتوى", fontWeight = FontWeight.Bold) },
            icon = { Icon(Icons.Default.CloudUpload, contentDescription = null) }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = { Text("المستخدمين والرتب", fontWeight = FontWeight.Bold) },
            icon = { Icon(Icons.Default.ManageAccounts, contentDescription = null) }
          )
          Tab(
            selected = selectedTab == 2,
            onClick = { selectedTab = 2 },
            text = { Text("التبليغات (${issueReports.size})", fontWeight = FontWeight.Bold) },
            icon = { Icon(Icons.Default.ReportProblem, contentDescription = null) }
          )
        }
      }

      if (statusMessage != null) {
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
          ) {
            Text(
              text = statusMessage ?: "",
              modifier = Modifier.padding(12.dp),
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      }

      // TAB 0: واجهة الرفع الموحدة لأنواع المحتوى الأربعة
      if (selectedTab == 0) {
        item {
          Text(
            text = "شاشة الرفع الموحدة (نشر فوري بثقة كاملة):",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
          )
        }

        item {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(
              Triple("محاضرة وملف دراسي", "رفع وتخزين ملفات PDF وملخصات المحاضرات", Icons.Default.Description),
              Triple("إعلان بيداغوجي", "نشر تنبيه عاجل أو هام للطلبة", Icons.Default.Campaign),
              Triple("حصة جدول دراسي", "إضافة أو تعديل توقيت وقاعة حصة", Icons.Default.CalendarMonth),
              Triple("امتحان واختبار", "برمجة موعد امتحان وقاعة ومدرج", Icons.Default.Science)
            ).forEach { (title, subtitle, icon) ->
              Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                onClick = {
                  uploadContentType = when {
                    title.contains("محاضرة") -> "محاضرة"
                    title.contains("إعلان") -> "إعلان"
                    title.contains("جدول") -> "حصة جدول"
                    else -> "امتحان"
                  }
                  showUploadDialog = true
                },
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(46.dp)
                      .clip(CircleShape)
                      .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                  }

                  Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                  }

                  Button(
                    onClick = {
                      uploadContentType = when {
                        title.contains("محاضرة") -> "محاضرة"
                        title.contains("إعلان") -> "إعلان"
                        title.contains("جدول") -> "حصة جدول"
                        else -> "امتحان"
                      }
                      showUploadDialog = true
                    },
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Text("رفع الآن")
                  }
                }
              }
            }
          }
        }
      }

      // TAB 1: قائمة المستخدمين والرتب والترقية
      if (selectedTab == 1) {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (currentRole == "OWNER") "كافة مستخدمي المنظومة والترقيات:" else "قائمة طلبة الفوج:",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
            )

            Button(
              onClick = { showAddStudentDialog = true },
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("إضافة طالب", fontSize = 12.sp)
            }
          }
        }

        items(users) { u ->
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
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
                      .size(38.dp)
                      .clip(CircleShape)
                      .background(
                        when (u.role) {
                          "OWNER" -> Color(0xFFE11D48)
                          "SPECIALTY_ADMIN" -> MaterialTheme.colorScheme.primary
                          "REPRESENTATIVE" -> Color(0xFF8B5CF6)
                          else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                      ),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = when (u.role) {
                        "OWNER" -> Icons.Default.Shield
                        "SPECIALTY_ADMIN" -> Icons.Default.SupervisorAccount
                        "REPRESENTATIVE" -> Icons.Default.School
                        else -> Icons.Default.Person
                      },
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(20.dp)
                    )
                  }

                  Column {
                    Text(u.fullName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text("${u.groupNumber} • ${u.email}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                  }
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  Text(
                    text = when (u.role) {
                      "OWNER" -> "المالك"
                      "SPECIALTY_ADMIN" -> "مسؤول تخصص"
                      "REPRESENTATIVE" -> "ممثل (${u.representativeScope})"
                      else -> "طالب"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                  )
                }
              }

              // Promotion buttons (Available for Owner & Specialty Admin)
              if (currentRole == "OWNER") {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  OutlinedButton(
                    onClick = {
                      viewModel.updateUserRole(u.id, "SPECIALTY_ADMIN", "تخصص كامل")
                      statusMessage = "تمت ترقية ${u.fullName} إلى مسؤول تخصص 🏛️"
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(4.dp)
                  ) {
                    Text("ترقية لمسؤول تخصص", fontSize = 10.sp)
                  }

                  OutlinedButton(
                    onClick = {
                      viewModel.updateUserRole(u.id, "REPRESENTATIVE", "فوج واحد")
                      statusMessage = "تم تعيين ${u.fullName} كممثل للفوج 🎓"
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(4.dp)
                  ) {
                    Text("تعيين كممثل", fontSize = 10.sp)
                  }
                }
              }
            }
          }
        }
      }

      // TAB 2: التبليغات الواردة من الطلاب
      if (selectedTab == 2) {
        item {
          Text(
            text = "التبليغات والشكاوى المباشرة الواردة من الطلبة:",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
          )
        }

        if (issueReports.isEmpty()) {
          item {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "لا توجد أي تبليغات واردة حالياً من الطلبة.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
              )
            }
          }
        } else {
          items(issueReports) { rep ->
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "من: ${rep.studentName} (${rep.studentGroup})",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                  )
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(if (rep.status == "تم الحل") Color(0xFF10B981) else Color(0xFFF59E0B))
                      .padding(horizontal = 8.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = rep.status,
                      style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                    )
                  }
                }

                Text(
                  text = "نوع المشكلة: ${rep.itemType} - ${rep.itemTitle}",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                  text = rep.description,
                  style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Button(
                    onClick = {
                      viewModel.updateIssueReportStatus(rep, "تم الحل", "تم تصحيح الملف/الجدول بنجاح")
                      statusMessage = "تم وضع البلاغ كـ [تم الحل] وإشعار الطالب."
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تم التصحيح والحل")
                  }

                  OutlinedButton(
                    onClick = { viewModel.deleteIssueReport(rep) },
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
