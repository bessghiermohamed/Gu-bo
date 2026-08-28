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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
  val selectedSpecialtyId by viewModel.selectedSpecialtyId.collectAsStateWithLifecycle()
  val selectedYearId by viewModel.selectedYearId.collectAsStateWithLifecycle()

  var showAddLectureDialog by remember { mutableStateOf(false) }
  var showAddAnnouncementDialog by remember { mutableStateOf(false) }
  var showAddModuleDialog by remember { mutableStateOf(false) }
  var showAddSpecialtyDialog by remember { mutableStateOf(false) }
  var showAddScheduleDialog by remember { mutableStateOf(false) }
  var showAddExamDialog by remember { mutableStateOf(false) }

  var statusMessage by remember { mutableStateOf<String?>(null) }

  // 1. Add Lecture Dialog
  if (showAddLectureDialog) {
    var selectedModId by remember { mutableStateOf(modules.firstOrNull()?.id ?: 1L) }
    var weekNumText by remember { mutableStateOf("4") }
    var titleText by remember { mutableStateOf("") }
    var summaryText by remember { mutableStateOf("") }
    var pdfNameText by remember { mutableStateOf("lecture_notes.pdf") }
    var durationText by remember { mutableStateOf("90") }
    var modExpanded by remember { mutableStateOf(false) }

    AlertDialog(
      onDismissRequest = { showAddLectureDialog = false },
      title = { Text("إضافة محاضرة جديدة ورفع PDF", fontWeight = FontWeight.Black) },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Module Picker
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

          OutlinedTextField(
            value = weekNumText,
            onValueChange = { weekNumText = it },
            label = { Text("رقم الأسبوع (مثال: 4)") },
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = titleText,
            onValueChange = { titleText = it },
            label = { Text("عنوان المحاضرة (مثال: الإعراب والبناء)") },
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = summaryText,
            onValueChange = { summaryText = it },
            label = { Text("ملخص المحاضرة ومحاور الدرس") },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = pdfNameText,
            onValueChange = { pdfNameText = it },
            label = { Text("اسم ملف الـ PDF المرفق") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (titleText.isNotBlank()) {
              viewModel.addLecture(
                moduleId = selectedModId,
                weekNumber = weekNumText.toIntOrNull() ?: 1,
                title = titleText,
                summary = summaryText.ifEmpty { "ملخص المحاضرة المرفقة بصيغة PDF." },
                pdfFileName = pdfNameText,
                durationMinutes = durationText.toIntOrNull() ?: 90
              )
              statusMessage = "تم نشر المحاضرة بنجاح وستظهر فوراً في تطبيق الطالب!"
            }
            showAddLectureDialog = false
          }
        ) {
          Text("نشر المحاضرة")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddLectureDialog = false }) { Text("إلغاء") }
      }
    )
  }

  // 2. Add Announcement Dialog (With Multiple Groups Scope Support)
  if (showAddAnnouncementDialog) {
    var titleText by remember { mutableStateOf("") }
    var contentText by remember { mutableStateOf("") }
    var authorText by remember { mutableStateOf("إدارة المدرسة العليا / الأستاذ") }
    var urgencyText by remember { mutableStateOf("عام") }
    var visibilityScope by remember { mutableStateOf("عدة أفواج محددة") }
    val availableGroups = listOf("الفوج 01", "الفوج 02", "الفوج 03", "الفوج 04")
    val selectedGroups = remember { mutableStateListOf("الفوج 01", "الفوج 03") }

    AlertDialog(
      onDismissRequest = { showAddAnnouncementDialog = false },
      title = { Text("نشر إعلان أكاديمي وتحديد نطاق الرؤية", fontWeight = FontWeight.Black) },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = titleText,
            onValueChange = { titleText = it },
            label = { Text("عنوان الإعلان") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = contentText,
            onValueChange = { contentText = it },
            label = { Text("نص وتفاصيل الإعلان") },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = authorText,
            onValueChange = { authorText = it },
            label = { Text("الجهة أو الأستاذ المصدر") },
            modifier = Modifier.fillMaxWidth()
          )

          Text("درجة الأهمية:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("عام", "هام", "عاجل").forEach { urg ->
              FilterChip(
                selected = urgencyText == urg,
                onClick = { urgencyText = urg },
                label = { Text(urg) }
              )
            }
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

          // Multiple Groups Visibility Scope
          Text("نطاق الرؤية والأفواج المستهدفة:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf("تخصص كامل", "عدة أفواج محددة", "فوج واحد").forEach { scope ->
              FilterChip(
                selected = visibilityScope == scope,
                onClick = { visibilityScope = scope },
                label = { Text(scope, fontSize = 11.sp) }
              )
            }
          }

          if (visibilityScope != "تخصص كامل") {
            Text(
              text = if (visibilityScope == "عدة أفواج محددة") "اختر الأفواج المعنية (اختيار متعدد):" else "اختر الفوج المعني:",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              availableGroups.forEach { group ->
                val isSelected = selectedGroups.contains(group)
                FilterChip(
                  selected = isSelected,
                  onClick = {
                    if (visibilityScope == "فوج واحد") {
                      selectedGroups.clear()
                      selectedGroups.add(group)
                    } else {
                      if (isSelected) {
                        if (selectedGroups.size > 1) selectedGroups.remove(group)
                      } else {
                        selectedGroups.add(group)
                      }
                    }
                  },
                  label = { Text(group, fontSize = 11.sp) },
                  leadingIcon = if (isSelected) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                  } else null
                )
              }
            }

            Text(
              text = "المستهدفون حالياً: ${selectedGroups.joinToString("، ")}",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
              )
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (titleText.isNotBlank()) {
              val targetGroupsStr = if (visibilityScope == "تخصص كامل") "الكل" else selectedGroups.joinToString("، ")
              viewModel.publishAnnouncement(
                title = titleText,
                content = contentText,
                author = authorText,
                urgency = urgencyText,
                visibilityScope = visibilityScope,
                targetGroups = targetGroupsStr
              )
              statusMessage = "تم نشر الإعلان بنطاق ($visibilityScope: $targetGroupsStr) بنجاح!"
            }
            showAddAnnouncementDialog = false
          }
        ) {
          Text("نشر الإعلان")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddAnnouncementDialog = false }) { Text("إلغاء") }
      }
    )
  }

  // 3. Add Module Dialog
  if (showAddModuleDialog) {
    var nameText by remember { mutableStateOf("") }
    var codeText by remember { mutableStateOf("") }
    var coeffText by remember { mutableStateOf("3.0") }
    var creditsText by remember { mutableStateOf("5") }
    var profText by remember { mutableStateOf("") }
    var profEmailText by remember { mutableStateOf("") }
    var categoryText by remember { mutableStateOf("أساسي") }
    var descText by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showAddModuleDialog = false },
      title = { Text("إضافة مقياس دراسي جديد", fontWeight = FontWeight.Black) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = nameText,
            onValueChange = { nameText = it },
            label = { Text("اسم المقياس (مثال: اللسانيات التطبيقية)") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = codeText,
            onValueChange = { codeText = it },
            label = { Text("رمز المقياس (مثال: ARA-208)") },
            modifier = Modifier.fillMaxWidth()
          )
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = coeffText,
              onValueChange = { coeffText = it },
              label = { Text("المعامل") },
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = creditsText,
              onValueChange = { creditsText = it },
              label = { Text("الأرصدة") },
              modifier = Modifier.weight(1f)
            )
          }
          OutlinedTextField(
            value = profText,
            onValueChange = { profText = it },
            label = { Text("اسم الأستاذ المحاضر") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (nameText.isNotBlank()) {
              viewModel.addModule(
                specialtyId = selectedSpecialtyId,
                yearId = selectedYearId,
                name = nameText,
                code = codeText.ifEmpty { "MOD-NEW" },
                coefficient = coeffText.toDoubleOrNull() ?: 2.0,
                credits = creditsText.toIntOrNull() ?: 4,
                professorName = profText,
                professorEmail = profEmailText,
                category = categoryText,
                description = descText
              )
              statusMessage = "تم إنشاء المقياس وإضافته لقائمة المقررات!"
            }
            showAddModuleDialog = false
          }
        ) {
          Text("حفظ المقياس")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddModuleDialog = false }) { Text("إلغاء") }
      }
    )
  }

  // 4. Add Specialty Dialog
  if (showAddSpecialtyDialog) {
    var specNameText by remember { mutableStateOf("") }
    var specCodeText by remember { mutableStateOf("") }
    var specDescText by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showAddSpecialtyDialog = false },
      title = { Text("إضافة تخصص أكاديمي جديد", fontWeight = FontWeight.Black) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = specNameText,
            onValueChange = { specNameText = it },
            label = { Text("اسم التخصص (مثال: الذكاء الاصطناعي)") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = specCodeText,
            onValueChange = { specCodeText = it },
            label = { Text("رمز التخصص (مثال: AI)") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = specDescText,
            onValueChange = { specDescText = it },
            label = { Text("نبذة عن التخصص") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (specNameText.isNotBlank()) {
              viewModel.addSpecialty(specNameText, specCodeText, specDescText)
              statusMessage = "تمت إضافة التخصص بنجاح!"
            }
            showAddSpecialtyDialog = false
          }
        ) {
          Text("إضافة التخصص")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddSpecialtyDialog = false }) { Text("إلغاء") }
      }
    )
  }

  // Status Notification Snackbar
  statusMessage?.let { msg ->
    AlertDialog(
      onDismissRequest = { statusMessage = null },
      title = { Text("تأكيد العملية ✓", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
      text = { Text(msg, style = MaterialTheme.typography.bodyMedium) },
      confirmButton = {
        Button(onClick = { statusMessage = null }) { Text("حسناً") }
      }
    )
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("admin_panel_screen"),
    contentPadding = PaddingValues(bottom = 90.dp, top = 8.dp, start = 16.dp, end = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Admin Master Banner
    item {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "لوحة التحكم الأكاديمي والإدارة",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
              )
              Text(
                text = "إدارة المقررات، نشر المحاضرات، ورفع ملفات PDF للطلاب",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )
            }

            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
              )
            }
          }
        }
      }
    }

    // 2. Quick Action Buttons Grid
    item {
      Text(
        text = "عمليات الإدارة السريعة",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
    }

    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Add Lecture Action
        AdminActionRow(
          title = "إضافة محاضرة جديدة (مع تحديد الأسبوع وPDF)",
          description = "المقررات → التخصص → المقياس → نشر المحاضرة والملف",
          icon = Icons.Default.UploadFile,
          onClick = { showAddLectureDialog = true }
        )

        // Publish Announcement Action
        AdminActionRow(
          title = "نشر إعلان فوري للطلاب",
          description = "تنبيهات إدارية، تغيير مواعيد الحصص، وأخبار الكلية",
          icon = Icons.Default.Campaign,
          onClick = { showAddAnnouncementDialog = true }
        )

        // Add Module Action
        AdminActionRow(
          title = "إضافة مقياس تعليمي جديد",
          description = "تحديد المعامل، عدد الأرصدة، واسم الأستاذ المشرف",
          icon = Icons.AutoMirrored.Filled.MenuBook,
          onClick = { showAddModuleDialog = true }
        )

        // Add Specialty Action
        AdminActionRow(
          title = "إضافة تخصص جامعي جديد",
          description = "إضافة مسار أكاديمي جديد مع سنوات ليسانس وماستر",
          icon = Icons.Default.School,
          onClick = { showAddSpecialtyDialog = true }
        )
      }
    }

    // 3. Return to Student App
    item {
      FilledTonalButton(
        onClick = { onNavigate(ScreenRoute.HOME) },
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("العودة إلى واجهة الطالب الرئيسية", fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
private fun AdminActionRow(
  title: String,
  description: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(MaterialTheme.colorScheme.surface)
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
          .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(24.dp)
        )
      }

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = description,
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
      }

      Button(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Text("إضافة", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
      }
    }
  }
}
