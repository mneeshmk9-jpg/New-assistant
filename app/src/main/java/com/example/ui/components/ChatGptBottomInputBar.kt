package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChatGptEmerald
import com.example.ui.theme.ChatGptInputBg

@Composable
fun ChatGptBottomInputBar(
    typedQuery: String,
    onQueryChanged: (String) -> Unit,
    onSend: (String) -> Unit,
    isThinking: Boolean,
    isWebSearchActive: Boolean,
    onToggleWebSearch: () -> Unit,
    onOpenVoiceMode: () -> Unit,
    onStopStreaming: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Main input pill box
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Web Search Globe Toggle Button
                IconButton(
                    onClick = onToggleWebSearch,
                    modifier = Modifier.size(36.dp).testTag("web_search_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = "Search Web",
                        tint = if (isWebSearchActive) ChatGptEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Text field
                TextField(
                    value = typedQuery,
                    onValueChange = onQueryChanged,
                    placeholder = {
                        Text(
                            text = if (isWebSearchActive) "Search web or ask anything..." else "Message ChatGPT...",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chatgpt_text_input"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    maxLines = 4
                )

                // Right Button: Voice Mode (when empty) or Send / Stop Button
                if (isThinking) {
                    // Stop streaming button
                    IconButton(
                        onClick = onStopStreaming,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onBackground)
                            .testTag("chatgpt_stop_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop generating",
                            tint = MaterialTheme.colorScheme.background,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else if (typedQuery.trim().isEmpty()) {
                    // Voice mode orb button
                    IconButton(
                        onClick = onOpenVoiceMode,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onBackground)
                            .testTag("chatgpt_bottom_voice_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = "Voice Mode",
                            tint = MaterialTheme.colorScheme.background,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    // Send Arrow Button
                    IconButton(
                        onClick = {
                            if (typedQuery.trim().isNotEmpty()) {
                                onSend(typedQuery.trim())
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ChatGptEmerald)
                            .testTag("chatgpt_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Send message",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // ChatGPT disclaimer footer note
        Text(
            text = "ChatGPT can make mistakes. Check important info.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 2.dp)
        )
    }
}
