package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.ChatSession
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatGptDrawer(
    sessions: List<ChatSession>,
    currentSessionId: String,
    isTemporaryChat: Boolean,
    onSelectSession: (String) -> Unit,
    onNewChat: () -> Unit,
    onRenameSession: (String, String) -> Unit,
    onTogglePinSession: (String, Boolean) -> Unit,
    onDeleteSession: (String) -> Unit,
    onToggleTemporaryChat: () -> Unit,
    onOpenCustomInstructions: () -> Unit,
    onOpenSettings: () -> Unit,
    onClearAllChats: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var sessionToRename by remember { mutableStateOf<ChatSession?>(null) }
    var renameText by remember { mutableStateOf("") }
    var showClearAllConfirm by remember { mutableStateOf(false) }

    val filteredSessions = remember(sessions, searchQuery) {
        if (searchQuery.isBlank()) {
            sessions
        } else {
            sessions.filter { it.title.contains(searchQuery, ignoreCase = true) }
        }
    }

    val pinnedSessions = filteredSessions.filter { it.isPinned }
    val regularSessions = filteredSessions.filter { !it.isPinned }

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, bottom = 8.dp)
        ) {
            // 1. Top Header: Brand & New Chat Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ChatGptEmerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "ChatGPT Logo",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "ChatGPT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onCloseDrawer,
                    modifier = Modifier.testTag("close_drawer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Drawer",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. New Chat Action Button (Full Width Card)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        onNewChat()
                        onCloseDrawer()
                    }
                    .testTag("drawer_new_chat_button"),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Chat",
                        tint = ChatGptEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "New chat",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Search Chats Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search chats...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear Search",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ChatGptEmerald,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Session History List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                // Pinned section
                if (pinnedSessions.isNotEmpty()) {
                    item {
                        Text(
                            text = "PINNED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    items(pinnedSessions, key = { "pinned_${it.id}" }) { session ->
                        DrawerSessionItem(
                            session = session,
                            isSelected = session.id == currentSessionId && !isTemporaryChat,
                            onSelect = {
                                onSelectSession(session.id)
                                onCloseDrawer()
                            },
                            onRename = {
                                sessionToRename = session
                                renameText = session.title
                            },
                            onTogglePin = { onTogglePinSession(session.id, session.isPinned) },
                            onDelete = { onDeleteSession(session.id) }
                        )
                    }
                }

                // Recent Chats section
                item {
                    Text(
                        text = "RECENT CHATS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                if (regularSessions.isEmpty() && pinnedSessions.isEmpty()) {
                    item {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No matching chats found." else "No chats yet. Start a conversation!",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
                        )
                    }
                } else {
                    items(regularSessions, key = { it.id }) { session ->
                        DrawerSessionItem(
                            session = session,
                            isSelected = session.id == currentSessionId && !isTemporaryChat,
                            onSelect = {
                                onSelectSession(session.id)
                                onCloseDrawer()
                            },
                            onRename = {
                                sessionToRename = session
                                renameText = session.title
                            },
                            onTogglePin = { onTogglePinSession(session.id, session.isPinned) },
                            onDelete = { onDeleteSession(session.id) }
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outline
            )

            // 5. Drawer Bottom Actions: Temporary Chat, Custom Instructions, Settings
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
                // Temporary chat toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleTemporaryChat() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Temporary Chat",
                            tint = if (isTemporaryChat) Color(0xFFFBBF24) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Temporary Chat",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Switch(
                        checked = isTemporaryChat,
                        onCheckedChange = { onToggleTemporaryChat() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = ChatGptEmerald
                        )
                    )
                }

                // Custom instructions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            onOpenCustomInstructions()
                            onCloseDrawer()
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("custom_instructions_drawer_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Custom Instructions",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Custom Instructions",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Settings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            onOpenSettings()
                            onCloseDrawer()
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("chatgpt_settings_drawer_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "ChatGPT Settings",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

    // Rename Dialog
    if (sessionToRename != null) {
        AlertDialog(
            onDismissRequest = { sessionToRename = null },
            title = { Text("Rename Chat") },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text("Chat Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (renameText.isNotBlank()) {
                            onRenameSession(sessionToRename!!.id, renameText.trim())
                        }
                        sessionToRename = null
                    }
                ) {
                    Text("Save", color = ChatGptEmerald)
                }
            },
            dismissButton = {
                TextButton(onClick = { sessionToRename = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Clear all chats confirmation
    if (showClearAllConfirm) {
        AlertDialog(
            onDismissRequest = { showClearAllConfirm = false },
            title = { Text("Clear All Chats?") },
            text = { Text("Are you sure you want to delete all chat history? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAllChats()
                        showClearAllConfirm = false
                    }
                ) {
                    Text("Clear All", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DrawerSessionItem(
    session: ChatSession,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onRename: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit
) {
    var isMenuOpen by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onSelect() },
        color = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (session.isPinned) Icons.Default.PushPin else Icons.Default.ChatBubbleOutline,
                    contentDescription = null,
                    tint = if (isSelected) ChatGptEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = session.title.ifBlank { "New Chat" },
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box {
                IconButton(
                    onClick = { isMenuOpen = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Session Options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }

                DropdownMenu(
                    expanded = isMenuOpen,
                    onDismissRequest = { isMenuOpen = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(if (session.isPinned) "Unpin chat" else "Pin chat") },
                        leadingIcon = {
                            Icon(Icons.Default.PushPin, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        onClick = {
                            onTogglePin()
                            isMenuOpen = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Rename") },
                        leadingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        onClick = {
                            onRename()
                            isMenuOpen = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete chat", color = Color.Red) },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
                        },
                        onClick = {
                            onDelete()
                            isMenuOpen = false
                        }
                    )
                }
            }
        }
    }
}
