package com.example.talib.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.talib.data.local.StudentProfile
import com.example.talib.ui.viewmodel.ScreenRoute
import com.example.talib.ui.viewmodel.TalibViewModel

@Composable
fun ProfileScreen(
  viewModel: TalibViewModel,
  onNavigate: (ScreenRoute) -> Unit
) {
  val profile by viewModel.studentProfile.collectAsStateWithLifecycle()
  val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
  val gpa by viewModel.calculatedGPA.collectAsStateWithLifecycle()

  var showEditProfileDialog by remember { mutableStateOf(false) }
  var showAdminPinDialog by remember { mutableStateOf(false) }
  var adminPinInput by remember { mutableStateOf("") }
  var adminPinError by remember { mutableStateOf(false) }

  // Admin PIN Protection Dialog
  if (showAdminPinDialog) {
    AlertDialog(
      onDismissRequest = {
        showAdminPinDialog = false
        adminPinInput = ""
        adminPinError = false
      },
      title = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Text("رمز الدخول الإداري", fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "هذه المساحة مخصصة للأساتذة وإدارة القسم لرفع المحاضرات ونشر الإعلانات.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          OutlinedTextField(
            value = adminPinInput,
            onValueChange = {
              adminPinInput = it
              adminPinError = false
            },
            label = { Text("رمز المرور (الافتراضي: 1234)") },
            isError = adminPinError,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          )
          if (adminPinError) {
            Text(
              text = "رمز المرور غير صحيح! حاول مرة أخرى.",
              color = MaterialTheme.colorScheme.error,
              style = MaterialTheme.typography.bodySmall
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (adminPinInput == "1234" || adminPinInput == "admin") {
              showAdminPinDialog = false
              adminPinInput = ""
              adminPinError = false
              onNavigate(ScreenRoute.ADMIN)
            } else {
              adminPinError = true
            }
          },
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("دخول للوحة الإدارة")
        }
      },
      dismissButton = {
        TextButton(onClick = {
          showAdminPinDialog = false
          adminPinInput = ""
          adminPinError = false
        }) {
          Text("إلغاء")
        }
      }
    )
  }

  // Edit Profile Dialog
  if (showEditProfileDialog && profile != null) {
    var nameText by remember { mutableStateOf(profile?.fullName ?: "") }
    var uniText by remember { mutableStateOf(profile?.university ?: "") }
    var groupText by remember { mutableStateOf(profile?.groupNumber ?: "") }
    var emailText by remember { mutableStateOf(profile?.email ?: "") }

    AlertDialog(
      onDismissRequest = { showEditProfileDialog = false },
      title = { Text("تعديل بيانات الطالب", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = nameText,
            onValueChange = { nameText = it },
            label = { Text("الاسم واللقب") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = uniText,
            onValueChange = { uniText = it },
            label = { Text("الجامعة") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = groupText,
            onValueChange = { groupText = it },
            label = { Text("الفوج الدراسي") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = emailText,
            onValueChange = { emailText = it },
            label = { Text("البريد الإلكتروني") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            profile?.let { p ->
              viewModel.updateProfile(
                p.copy(
                  fullName = nameText,
                  university = uniText,
                  groupNumber = groupText,
                  email = emailText
                )
              )
            }
            showEditProfileDialog = false
          },
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("حفظ التغييرات")
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditProfileDialog = false }) {
          Text("إلغاء")
        }
      }
    )
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("profile_screen"),
    contentPadding = PaddingValues(bottom = 90.dp, top = 8.dp, start = 16.dp, end = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Digital Student Card
    item {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "بطاقة الطالب الرقمية",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.8f))
              )
              Text(
                text = profile?.fullName ?: "محمد البشير بن علي",
                style = MaterialTheme.typography.titleLarge.copy(
                  color = Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 20.sp
                )
              )
            }

            Box(
              modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(30.dp)
              )
            }
          }

          HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = "رقم التسجيل الجامعي",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f))
              )
              Text(
                text = profile?.studentId ?: "202631084592",
                style = MaterialTheme.typography.labelMedium.copy(
                  color = Color.White,
                  fontWeight = FontWeight.Bold
                )
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "الفوج والمستوى",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f))
              )
              Text(
                text = "${profile?.groupNumber ?: "الفوج 03"} • ${profile?.academicYearName ?: "L2"}",
                style = MaterialTheme.typography.labelMedium.copy(
                  color = Color.White,
                  fontWeight = FontWeight.Bold
                )
              )
            }
          }

          Text(
            text = "${profile?.university ?: "جامعة الجزائر 1"} • ${profile?.faculty ?: "كلية الآداب"}",
            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
          )
        }
      }
    }

    // 2. Profile Actions
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Text(
            text = "إعدادات الحساب والمظهر",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )

          // Edit Profile item
          ListItem(
            headlineContent = { Text("تعديل المعلومات الشخصية", fontWeight = FontWeight.SemiBold) },
            supportingContent = { Text(profile?.email ?: "mohamedbessghier8@gmail.com") },
            leadingContent = {
              Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            trailingContent = {
              IconButton(onClick = { showEditProfileDialog = true }) {
                Icon(Icons.Default.ArrowBackIos, contentDescription = null, modifier = Modifier.size(16.dp))
              }
            }
          )

          HorizontalDivider()

          // Dark Mode Toggle
          ListItem(
            headlineContent = { Text("الوضع الليلي / النهاري", fontWeight = FontWeight.SemiBold) },
            supportingContent = { Text(if (isDarkMode) "الوضع الداكن (القرمزي الأحمر)" else "الوضع الفاتح (البنفسجي الأرجواني)") },
            leadingContent = {
              Icon(
                imageVector = if (isDarkMode) Icons.Default.WbSunny else Icons.Default.NightlightRound,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
              )
            },
            trailingContent = {
              Switch(
                checked = isDarkMode,
                onCheckedChange = { viewModel.toggleTheme() }
              )
            }
          )
        }
      }
    }

    // 3. Admin / Professor Portal Launcher Card
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
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
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.AdminPanelSettings,
                  contentDescription = null,
                  tint = Color.White
                )
              }

              Column {
                Text(
                  text = "بوابة إدارة المحتوى والأستاذ",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black)
                )
                Text(
                  text = "إضافة مقاييس، محاضرات، ملفات PDF، وإعلانات",
                  style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
              }
            }
          }

          Button(
            onClick = { showAdminPinDialog = true },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("دخول الأساتذة والإدارة (بوابة محمية)", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
