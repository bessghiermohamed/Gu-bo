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
  val viewedLectures by viewModel.previouslyViewedLectures.collectAsStateWithLifecycle()
  val cachedMaterials by viewModel.allCachedMaterials.collectAsStateWithLifecycle()
  val isOfflineMode by viewModel.isOfflineMode.collectAsStateWithLifecycle()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("home_screen"),
    contentPadding = PaddingValues(bottom = 90.dp, top = 8.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Hero Card with Student Greeting & Academic Info
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
                imageVector = Icons.Default.Analytics,
                contentDescription = null,
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = "المعدل الفصلي التقديري: ",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
              )
              Text(
                text = String.format("%.2f / 20", gpa),
                style = MaterialTheme.typography.labelMedium.copy(
                  color = Color(0xFFFBBF24),
                  fontWeight = FontWeight.Black
                )
              )
            }

            Text(
              text = "${modules.size} مقاييس مسجلة",
              style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Bold
              )
            )
          }
        }
      }
    }

    // 1.5. 24/7 Firebase Genkit AI Assistant Card
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .clickable { onNavigate(ScreenRoute.AI_CHAT) }
          .testTag("home_ai_assistant_card")
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Box(
            modifier = Modifier
              .size(50.dp)
              .clip(CircleShape)
              .background(
                Brush.linearGradient(
                  listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.tertiary
                  )
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "AI",
              tint = Color.White,
              modifier = Modifier.size(26.dp)
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "المساعد الأكاديمي الذكي 24/7",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
              )
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(MaterialTheme.colorScheme.primary)
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "Genkit AI",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
              text = "دعم أكاديمي مباشر: شرح المحاضرات، حل تمارين الأعمال الموجهة TD، والتحضير للامتحانات.",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                text = "اسأل المساعد الآن",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.Bold
                )
              )
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }
    }

    // 1.6. Offline Room Database Cache Status Card
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isOfflineMode) Color(0xFFFEF3C7) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .clickable { onNavigate(ScreenRoute.OFFLINE_CACHE) }
          .testTag("home_offline_cache_card")
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(if (isOfflineMode) Color(0xFFF59E0B) else Color(0xFF10B981).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isOfflineMode) Icons.Default.WifiOff else Icons.Default.OfflinePin,
              contentDescription = null,
              tint = if (isOfflineMode) Color.White else Color(0xFF10B981),
              modifier = Modifier.size(22.dp)
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "المحتوى المخزن بدون إنترنت (Room DB)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
              if (isOfflineMode) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFD97706))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                  Text(
                    text = "غير متصل",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 9.sp)
                  )
                }
              }
            }

            Text(
              text = "${viewedLectures.size + cachedMaterials.size} محاضرة وملخص جاهزة للعرض الفوري بدون شبكة.",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
          }

          TextButton(onClick = { onNavigate(ScreenRoute.OFFLINE_CACHE) }) {
            Text("فتح الذاكرة")
          }
        }
      }
    }

    // 2. The 10 Main Functional Grid Cards (Faithful to prototype)
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "الخدمات والوحدات الأكاديمية",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
        )

        // Row 1: المقررات + المحاضرات والملفات
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          GridActionCard(
            title = "المقررات",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            badgeText = "${modules.size} مقاييس",
            isDarkMode = isDarkMode,
            delayOffsetMs = 0,
            onClick = { onNavigate(ScreenRoute.COURSES) },
            modifier = Modifier.weight(1f)
          )

          GridActionCard(
            title = "المحاضرات",
            icon = Icons.Default.Description,
            badgeText = "ملفات PDF",
            isDarkMode = isDarkMode,
            delayOffsetMs = 400,
            onClick = { onNavigate(ScreenRoute.LECTURES) },
            modifier = Modifier.weight(1f)
          )
        }

        // Row 2: الواجبات + الجدول
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          GridActionCard(
            title = "الواجبات",
            icon = Icons.Default.EditNote,
            badgeText = "مهام دراسية",
            isDarkMode = isDarkMode,
            delayOffsetMs = 200,
            onClick = { onNavigate(ScreenRoute.ASSIGNMENTS) },
            modifier = Modifier.weight(1f)
          )

          GridActionCard(
            title = "الجدول",
            icon = Icons.Default.CalendarMonth,
            badgeText = "التوقيت الأسبوعي",
            isDarkMode = isDarkMode,
            delayOffsetMs = 600,
            onClick = { onNavigate(ScreenRoute.SCHEDULE) },
            modifier = Modifier.weight(1f)
          )
        }

        // Row 3: الامتحانات + العلامات
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          GridActionCard(
            title = "الامتحانات",
            icon = Icons.Default.Science,
            badgeText = "${exams.size} اختبارات",
            isDarkMode = isDarkMode,
            delayOffsetMs = 300,
            onClick = { onNavigate(ScreenRoute.EXAMS) },
            modifier = Modifier.weight(1f)
          )

          GridActionCard(
            title = "العلامات",
            icon = Icons.Default.Equalizer,
            badgeText = "حساب المعدل",
            isDarkMode = isDarkMode,
            delayOffsetMs = 700,
            onClick = { onNavigate(ScreenRoute.GRADES) },
            modifier = Modifier.weight(1f)
          )
        }

        // Row 4: الفوج + الإعلانات
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          GridActionCard(
            title = "الفوج",
            icon = Icons.Default.Groups,
            badgeText = "الدفعة والأساتذة",
            isDarkMode = isDarkMode,
            delayOffsetMs = 150,
            onClick = { onNavigate(ScreenRoute.GROUP) },
            modifier = Modifier.weight(1f)
          )

          GridActionCard(
            title = "الإعلانات",
            icon = Icons.Default.Campaign,
            badgeText = "${announcements.size} تنبيهات",
            isDarkMode = isDarkMode,
            delayOffsetMs = 550,
            onClick = { onNavigate(ScreenRoute.ANNOUNCEMENTS) },
            modifier = Modifier.weight(1f)
          )
        }

        // Row 5: ملفاتي وملاحظاتي + المساعد الذكي AI
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          GridActionCard(
            title = "ملفاتي وملاحظاتي",
            icon = Icons.Default.FolderSpecial,
            badgeText = "محفوظاتي وملاحظاتي",
            isDarkMode = isDarkMode,
            delayOffsetMs = 250,
            onClick = { onNavigate(ScreenRoute.MY_FILES) },
            modifier = Modifier.weight(1f)
          )

          GridActionCard(
            title = "المساعد الذكي",
            icon = Icons.Default.AutoAwesome,
            badgeText = "Genkit AI 24/7",
            isDarkMode = isDarkMode,
            delayOffsetMs = 650,
            onClick = { onNavigate(ScreenRoute.AI_CHAT) },
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 3. Upcoming Schedule Section
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

    // 4. Latest Announcements Carousel
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
