package com.example.ui.components

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatGptTopBar(
    selectedModel: String,
    isTemporaryChat: Boolean,
    onModelSelected: (String) -> Unit,
    onOpenDrawer: () -> Unit,
    onNewChat: () -> Unit,
    onOpenVoiceMode: () -> Unit,
    onToggleTools: () -> Unit,
    activeTab: Int
) {
    var isModelMenuOpen by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Drawer menu button
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier.testTag("chatgpt_drawer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Open Chat History",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Center: Model Selector Dropdown Button
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { isModelMenuOpen = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("model_selector_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedModel,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select Model",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Model dropdown menu
                    DropdownMenu(
                        expanded = isModelMenuOpen,
                        onDismissRequest = { isModelMenuOpen = false },
                        modifier = Modifier
                            .width(280.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = "Model",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                        AVAILABLE_MODELS.forEach { model ->
                            val isSelected = model.id == selectedModel
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = model.name,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) ChatGptEmerald else MaterialTheme.colorScheme.onSurface,
                                                fontSize = 15.sp
                                            )
                                            if (model.badge != null) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Surface(
                                                    color = if (isSelected) ChatGptEmerald.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = model.badge,
                                                        fontSize = 10.sp,
                                                        color = if (isSelected) ChatGptEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                                                        fontWeight = FontWeight.SemiBold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = model.subtitle,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 14.sp
                                        )
                                    }
                                },
                                trailingIcon = {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = ChatGptEmerald,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                },
                                onClick = {
                                    onModelSelected(model.id)
                                    isModelMenuOpen = false
                                }
                            )
                        }
                    }
                }

                // Right Actions: Voice Mode, Tools, New Chat
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Tools toggle button (Smart Home, Reminder, Memories, Governance)
                    IconButton(
                        onClick = onToggleTools,
                        modifier = Modifier.testTag("tools_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (activeTab != 3) Icons.Default.ChatBubbleOutline else Icons.Default.Dashboard,
                            contentDescription = if (activeTab != 3) "Back to Chat" else "Open Tools",
                            tint = if (activeTab != 3) ChatGptEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Voice Mode button
                    IconButton(
                        onClick = onOpenVoiceMode,
                        modifier = Modifier.testTag("top_voice_mode_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = "Voice Mode",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // New Chat icon
                    IconButton(
                        onClick = onNewChat,
                        modifier = Modifier.testTag("top_new_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "New Chat",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Temporary Chat Banner if active
            if (isTemporaryChat) {
                Surface(
                    color = Color(0xFF374151),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Temporary Chat Active",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Temporary Chat: Won't appear in history or be used for training",
                            fontSize = 12.sp,
                            color = Color(0xFFF3F4F6)
                        )
                    }
                }
            }
        }
    }
}
