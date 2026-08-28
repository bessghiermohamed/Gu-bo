package com.example.talib.ai

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class ChatMessage(
  val id: String = UUID.randomUUID().toString(),
  val text: String,
  val isFromUser: Boolean,
  val timestamp: Long = System.currentTimeMillis(),
  val timeFormatted: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
  val categoryBadge: String? = null,
  val suggestedActions: List<String> = emptyList(),
  val isOfflineHandled: Boolean = false
)
