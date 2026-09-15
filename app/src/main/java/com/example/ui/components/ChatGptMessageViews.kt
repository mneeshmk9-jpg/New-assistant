package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.ConversationMessage
import com.example.ui.theme.*

@Composable
fun ChatGptMessageItem(
    message: ConversationMessage,
    isLatestAssistantMessage: Boolean,
    onRateMessage: (Int, Int) -> Unit,
    onRegenerate: () -> Unit,
    onSpeak: (String) -> Unit,
    onEditPrompt: (Int, String) -> Unit,
    onDeleteMessage: (Int) -> Unit
) {
    val context = LocalContext.current
    val isUser = message.role.equals("user", ignoreCase = true)

    if (isUser) {
        UserMessageView(
            message = message,
            onEditPrompt = { newText -> onEditPrompt(message.id, newText) }
        )
    } else {
        AssistantMessageView(
            message = message,
            isLatestAssistantMessage = isLatestAssistantMessage,
            onRateMessage = { rating -> onRateMessage(message.id, rating) },
            onRegenerate = onRegenerate,
            onSpeak = { onSpeak(message.text) }
        )
    }
}

@Composable
fun UserMessageView(
    message: ConversationMessage,
    onEditPrompt: (String) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(message.text) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Surface(
                color = ChatGptUserBubble,
                shape = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
                modifier = Modifier.testTag("user_message_bubble")
            ) {
                Text(
                    text = message.text,
                    color = ChatGptText,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }

            // Edit prompt small button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                IconButton(
                    onClick = {
                        editText = message.text
                        showEditDialog = true
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Message",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Message") },
            text = {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChatGptEmerald
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editText.isNotBlank()) {
                            onEditPrompt(editText.trim())
                        }
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChatGptEmerald)
                ) {
                    Text("Send")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AssistantMessageView(
    message: ConversationMessage,
    isLatestAssistantMessage: Boolean,
    onRateMessage: (Int) -> Unit,
    onRegenerate: () -> Unit,
    onSpeak: () -> Unit
) {
    val context = LocalContext.current
    var isThoughtExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        // Iconic Emerald ChatGPT Avatar
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(ChatGptEmerald),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "ChatGPT",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Model tag if available
            if (!message.modelUsed.isNullOrBlank()) {
                Text(
                    text = message.modelUsed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ChatGptEmerald,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            // Reasoning Accordion (o3 / o1 style)
            if (!message.thought.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ChatGptReasoningBg,
                    border = BorderStroke(1.dp, ChatGptEmerald.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isThoughtExpanded = !isThoughtExpanded }
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = "Reasoning Process",
                                    tint = ChatGptEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Thought process (Chain-of-Thought)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ChatGptEmerald
                                )
                            }
                            Icon(
                                imageVector = if (isThoughtExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = ChatGptEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        AnimatedVisibility(visible = isThoughtExpanded) {
                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                Text(
                                    text = message.thought,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            // Render message content (parsed with code blocks if applicable)
            ParsedMessageContent(rawText = message.text)

            // Action Toolbar (Copy, TTS, Thumbs up/down, Regenerate)
            if (message.text.isNotBlank() && message.text != "...") {
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Copy Response
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("ChatGPT response", message.text))
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy response",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // Read Aloud (TTS)
                    IconButton(
                        onClick = onSpeak,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Read aloud",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Thumbs Up
                    IconButton(
                        onClick = { onRateMessage(if (message.rating == 1) 0 else 1) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Good response",
                            tint = if (message.rating == 1) ChatGptEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // Thumbs Down
                    IconButton(
                        onClick = { onRateMessage(if (message.rating == -1) 0 else -1) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbDown,
                            contentDescription = "Bad response",
                            tint = if (message.rating == -1) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // Regenerate (if latest assistant response)
                    if (isLatestAssistantMessage) {
                        IconButton(
                            onClick = onRegenerate,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Regenerate response",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ParsedMessageContent(rawText: String) {
    val context = LocalContext.current
    // Split by code blocks ```...```
    val parts = remember(rawText) { splitByCodeBlocks(rawText) }

    Column(modifier = Modifier.fillMaxWidth()) {
        parts.forEach { part ->
            if (part.isCode) {
                // Render Styled Code Block
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ChatGptCodeBg,
                    border = BorderStroke(1.dp, Color(0xFF383838)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column {
                        // Code Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ChatGptCodeHeader)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = part.language.ifBlank { "code" },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFD4D4D4)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Code", part.content))
                                        Toast.makeText(context, "Code copied", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy code",
                                    tint = Color(0xFFD4D4D4),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Copy code",
                                    fontSize = 11.sp,
                                    color = Color(0xFFD4D4D4)
                                )
                            }
                        }

                        // Code Body
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(12.dp)
                        ) {
                            Text(
                                text = part.content,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                color = Color(0xFFE6EDF3),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            } else {
                // Normal Prose Text
                Text(
                    text = part.content,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

data class MessageChunk(val isCode: Boolean, val content: String, val language: String = "")

private fun splitByCodeBlocks(text: String): List<MessageChunk> {
    val chunks = mutableListOf<MessageChunk>()
    val pattern = "```([a-zA-Z0-9_-]*)\\n?([\\s\\S]*?)```".toRegex()
    var lastIndex = 0

    pattern.findAll(text).forEach { match ->
        val start = match.range.first
        val end = match.range.last + 1

        if (start > lastIndex) {
            val prose = text.substring(lastIndex, start)
            if (prose.isNotBlank()) {
                chunks.add(MessageChunk(isCode = false, content = prose))
            }
        }

        val lang = match.groupValues[1].trim()
        val code = match.groupValues[2]
        chunks.add(MessageChunk(isCode = true, content = code, language = lang))
        lastIndex = end
    }

    if (lastIndex < text.length) {
        val remaining = text.substring(lastIndex)
        if (remaining.isNotBlank()) {
            chunks.add(MessageChunk(isCode = false, content = remaining))
        }
    }

    if (chunks.isEmpty() && text.isNotEmpty()) {
        chunks.add(MessageChunk(isCode = false, content = text))
    }

    return chunks
}

@Composable
fun ChatGptWelcomeState(
    onPromptSuggestionClicked: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // OpenAI / ChatGPT Logo Orb
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(ChatGptEmerald),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "ChatGPT",
                tint = Color.White,
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "What can I help with?",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Prompt suggestions
        val suggestions = listOf(
            "Write a Python script for web scraping",
            "Explain quantum computing like I'm 10",
            "Help me write a professional email",
            "Brainstorm product marketing ideas"
        )

        suggestions.forEach { prompt ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onPromptSuggestionClicked(prompt) },
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = prompt,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
