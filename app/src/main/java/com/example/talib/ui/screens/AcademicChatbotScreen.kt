package com.example.talib.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.talib.ai.ChatMessage
import com.example.talib.ui.viewmodel.ScreenRoute
import com.example.talib.ui.viewmodel.TalibViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicChatbotScreen(
  viewModel: TalibViewModel,
  onNavigateBack: () -> Unit
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val listState = rememberLazyListState()

  val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
  val isThinking by viewModel.isAiThinking.collectAsStateWithLifecycle()
  val currentInput by viewModel.currentChatInput.collectAsStateWithLifecycle()
  val isOfflineMode by viewModel.isOfflineMode.collectAsStateWithLifecycle()
  val profile by viewModel.studentProfile.collectAsStateWithLifecycle()

  // Auto-scroll to bottom when new message arrives
  LaunchedEffect(messages.size, isThinking) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  val quickPrompts = listOf(
    "📚 اشرح لي درس النحو (المبتدأ والخبر)",
    "💻 كيف تعمل خوارزمية البحث الثنائي؟",
    "📊 كيف أحسب معدلي الفصلي في نظام LMD؟",
    "🎯 نصائح ذكية للتحضير للامتحانات",
    "📝 منهجية توثيق المراجع والبحوث"
  )

  Scaffold(
    topBar = {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 4.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = onNavigateBack,
              modifier = Modifier.testTag("chat_back_button")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "رجوع",
                tint = MaterialTheme.colorScheme.onSurface
              )
            }

            // Bot Avatar with green glowing pulse indicator
            Box(contentAlignment = Alignment.BottomEnd) {
              Box(
                modifier = Modifier
                  .size(44.dp)
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
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(24.dp)
                )
              }

              // Online status dot
              Box(
                modifier = Modifier
                  .size(12.dp)
                  .clip(CircleShape)
                  .background(if (isOfflineMode) Color(0xFFF59E0B) else Color(0xFF10B981))
                  .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = "المساعد الأكاديمي الذكي",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp
                  )
                )
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "24/7 AI",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = MaterialTheme.colorScheme.primary,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp
                    )
                  )
                }
              }

              Text(
                text = if (isOfflineMode)
                  "وضع عدم الاتصال • يعمل من قاعدة المعرفة المحلية"
                else
                  "مدعوم بـ Firebase Genkit AI • جاهز دائماً لمساعدتك",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp
                )
              )
            }

            IconButton(
              onClick = { viewModel.clearChatHistory() },
              modifier = Modifier.testTag("clear_chat_button")
            ) {
              Icon(
                imageVector = Icons.Outlined.DeleteSweep,
                contentDescription = "مسح المحادثة",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          // Offline Notice Banner if toggled
          if (isOfflineMode) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFEF3C7))
                .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.WifiOff,
                  contentDescription = null,
                  tint = Color(0xFFD97706),
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = "وضع العمل بدون إنترنت نشط: يقدم المساعد الإجابات الأكاديمية والملخصات المخزنة محلياً.",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF92400E),
                    fontSize = 11.sp
                  )
                )
              }
            }
          }
        }
      }
    },
    bottomBar = {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
          // Quick Prompts Chips
          LazyRow(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(quickPrompts) { prompt ->
              SuggestionChip(
                onClick = { viewModel.askPredefinedAcademicQuestion(prompt) },
                label = {
                  Text(
                    text = prompt,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
                  )
                },
                colors = SuggestionChipDefaults.suggestionChipColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
              )
            }
          }

          // Input Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = currentInput,
              onValueChange = { viewModel.updateChatInput(it) },
              modifier = Modifier
                .weight(1f)
                .testTag("chat_input_field"),
              placeholder = {
                Text(
                  text = "اسأل عن أي درس، ملخص، أو مسألة أكاديمية...",
                  style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
                )
              },
              shape = RoundedCornerShape(24.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
              ),
              maxLines = 4
            )

            // Send Button
            FilledIconButton(
              onClick = {
                viewModel.sendAcademicChatMessage()
              },
              enabled = currentInput.isNotBlank() && !isThinking,
              modifier = Modifier
                .size(48.dp)
                .testTag("chat_send_button"),
              colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
              )
            ) {
              if (isThinking) {
                CircularProgressIndicator(
                  modifier = Modifier.size(20.dp),
                  color = MaterialTheme.colorScheme.primary,
                  strokeWidth = 2.dp
                )
              } else {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.Send,
                  contentDescription = "إرسال"
                )
              }
            }
          }
        }
      }
    }
  ) { paddingValues ->
    LazyColumn(
      state = listState,
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 14.dp)
        .testTag("chat_messages_list"),
      contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Academic Header Badge
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "دعم أكاديمي مخصص لتخصص: ${profile?.specialtyName ?: "الأدب العربي واللغات"}",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "يمكنك طلب شرح أي عنصر من المقررات أو الاستفسار عن تفاصيل الواجبات والامتحانات.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )
            }
          }
        }
      }

      // Chat Messages
      items(messages, key = { it.id }) { message ->
        ChatMessageBubble(
          message = message,
          onActionClick = { actionText ->
            viewModel.sendAcademicChatMessage(actionText)
          },
          onCopy = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Academic Note", message.text))
            Toast.makeText(context, "تم نسخ النص الأكاديمي بنجاح", Toast.LENGTH_SHORT).show()
          }
        )
      }

      // Thinking Animation
      if (isThinking) {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
              shape = RoundedCornerShape(18.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
              modifier = Modifier.padding(vertical = 4.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                PulsingDotsIndicator()
                Text(
                  text = "المساعد يحلل السؤال ويكتب الإجابة...",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                  )
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ChatMessageBubble(
  message: ChatMessage,
  onActionClick: (String) -> Unit,
  onCopy: () -> Unit
) {
  val isUser = message.isFromUser

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    verticalAlignment = Alignment.Top
  ) {
    if (!isUser) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(18.dp)
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
    }

    Column(
      modifier = Modifier.widthIn(max = 320.dp),
      horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
      Surface(
        shape = RoundedCornerShape(
          topStart = 18.dp,
          topEnd = 18.dp,
          bottomStart = if (isUser) 18.dp else 4.dp,
          bottomEnd = if (isUser) 4.dp else 18.dp
        ),
        color = if (isUser)
          MaterialTheme.colorScheme.primary
        else
          MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
        tonalElevation = if (isUser) 4.dp else 1.dp,
        modifier = Modifier.shadow(
          elevation = if (isUser) 3.dp else 1.dp,
          shape = RoundedCornerShape(18.dp)
        )
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          if (!isUser && message.categoryBadge != null) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = message.categoryBadge,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  )
                )
              }

              IconButton(
                onClick = onCopy,
                modifier = Modifier.size(24.dp)
              ) {
                Icon(
                  imageVector = Icons.Outlined.ContentCopy,
                  contentDescription = "نسخ النص",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          }

          Text(
            text = message.text,
            style = MaterialTheme.typography.bodyMedium.copy(
              color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
              lineHeight = 22.sp
            )
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = message.timeFormatted,
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (isUser) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
              fontSize = 10.sp
            ),
            modifier = Modifier.align(if (isUser) Alignment.End else Alignment.Start)
          )
        }
      }

      // Suggested follow-up chips if available
      if (!isUser && message.suggestedActions.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          message.suggestedActions.forEach { action ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .clickable { onActionClick(action) }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.SubdirectoryArrowLeft,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(14.dp)
                )
                Text(
                  text = action,
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

    if (isUser) {
      Spacer(modifier = Modifier.width(8.dp))
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Person,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

@Composable
private fun PulsingDotsIndicator() {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_dots")

  val dotAlpha1 by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "dot1"
  )

  val dotAlpha2 by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, delayMillis = 200, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "dot2"
  )

  val dotAlpha3 by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, delayMillis = 400, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "dot3"
  )

  Row(
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(6.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primary.copy(alpha = dotAlpha1))
    )
    Box(
      modifier = Modifier
        .size(6.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primary.copy(alpha = dotAlpha2))
    )
    Box(
      modifier = Modifier
        .size(6.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primary.copy(alpha = dotAlpha3))
    )
  }
}
