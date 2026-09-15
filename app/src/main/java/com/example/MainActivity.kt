package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.ui.components.AVAILABLE_MODELS
import com.example.ui.components.AVAILABLE_VOICES
import com.example.ui.components.AVAILABLE_PERSONAS
import com.example.ui.components.ChatGptBottomInputBar
import com.example.ui.components.ChatGptCustomInstructionsDialog
import com.example.ui.components.ChatGptDrawer
import com.example.ui.components.ChatGptMessageItem
import com.example.ui.components.ChatGptSettingsDialog
import com.example.ui.components.ChatGptTopBar
import com.example.ui.components.ChatGptVoiceModeDialog
import com.example.ui.components.ChatGptWelcomeState
import com.example.database.ConversationMessage
import com.example.database.MemoryItem
import com.example.database.ReminderItem
import com.example.database.SmartDevice
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AssistantViewModel
import com.example.viewmodel.AgentStep
import com.example.viewmodel.StepStatus
import com.example.viewmodel.ProposedAction
import com.example.viewmodel.ContactMatch
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.CircularProgressIndicator
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                RohitAssistantApp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Sync the dynamic governance permissions state with the physical OS permission status on app resume!
        try {
            val viewModel = androidx.lifecycle.ViewModelProvider(this).get(com.example.viewmodel.AssistantViewModel::class.java)
            viewModel.syncPermissionsWithSystem()
        } catch (e: Exception) {
            android.util.Log.e("RohitAI", "Failed to sync permissions onResume", e)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RohitAssistantApp(viewModel: AssistantViewModel = viewModel()) {
    val context = LocalContext.current
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasMicPermission = isGranted
            if (isGranted) {
                viewModel.startListening()
            } else {
                Toast.makeText(context, "Microphone permission is required for voice commands.", Toast.LENGTH_LONG).show()
            }
        }
    )

    var hasSmsPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val smsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasSmsPermission = isGranted
            if (isGranted) {
                Toast.makeText(context, "SMS permission granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "SMS sending commands will fail without permission.", Toast.LENGTH_LONG).show()
            }
        }
    )

    var hasContactsPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val contactsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasContactsPermission = isGranted
            if (isGranted) {
                Toast.makeText(context, "Contacts lookup granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Contacts lookup will fallback to mock entries without permission.", Toast.LENGTH_LONG).show()
            }
        }
    )

    // State bindings
    val isListening by viewModel.isListening.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val interimText by viewModel.interimSpeechText.collectAsState()

    // ChatGPT states
    val allSessions by viewModel.allSessions.collectAsState()
    val currentSessionId by viewModel.currentSessionId.collectAsState()
    val currentModel by viewModel.currentModel.collectAsState()
    val isTemporaryChat by viewModel.isTemporaryChat.collectAsState()
    val isWebSearchActive by viewModel.isWebSearchActive.collectAsState()
    val customInstructionsUser by viewModel.customInstructionsUser.collectAsState()
    val customInstructionsResponse by viewModel.customInstructionsResponse.collectAsState()
    val selectedVoice by viewModel.selectedVoice.collectAsState()
    val currentPersona by viewModel.currentPersona.collectAsState()
    val isVoiceModeOpen by viewModel.isVoiceModeOpen.collectAsState()
    val sessionMessages by viewModel.currentSessionMessages.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var showCustomInstructionsDialog by remember { mutableStateOf(false) }
    var showChatGptSettingsDialog by remember { mutableStateOf(false) }

    val messages by viewModel.messages.collectAsState()
    val memories by viewModel.memories.collectAsState()
    val devices by viewModel.devices.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    val agentSteps by viewModel.agentSteps.collectAsState()
    val pendingActions by viewModel.pendingActions.collectAsState()
    val hasRecoveryPending by viewModel.hasRecoveryPending.collectAsState()

    // Screen toggles - default to tab 3 (ChatGPT conversation)
    var activeTab by remember { mutableStateOf(3) } // 0: Smart Home, 1: Reminders, 2: Memories, 3: Chat Log
    var showSettings by remember { mutableStateOf(false) }
    var useKeyboardMode by remember { mutableStateOf(false) }
    var typedQuery by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    // Auto-scroll chat history on new message
    LaunchedEffect(sessionMessages.size) {
        if (sessionMessages.isNotEmpty()) {
            listState.animateScrollToItem(sessionMessages.size - 1)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                ChatGptDrawer(
                    sessions = allSessions,
                    currentSessionId = currentSessionId,
                    isTemporaryChat = isTemporaryChat,
                    onSelectSession = { id ->
                        viewModel.selectSession(id)
                        activeTab = 3
                    },
                    onNewChat = {
                        viewModel.createNewChat()
                        activeTab = 3
                    },
                    onRenameSession = { id, title -> viewModel.renameSession(id, title) },
                    onTogglePinSession = { id, isPinned -> viewModel.togglePinSession(id, isPinned) },
                    onDeleteSession = { id -> viewModel.deleteSession(id) },
                    onToggleTemporaryChat = { viewModel.toggleTemporaryChat() },
                    onOpenCustomInstructions = { showCustomInstructionsDialog = true },
                    onOpenSettings = { showChatGptSettingsDialog = true },
                    onClearAllChats = { viewModel.clearAllChats() },
                    onCloseDrawer = { coroutineScope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            // Modern Dark Background Canvas
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // 1. ChatGPT Top Bar with Model Dropdown, Voice Mode, Drawer button
                    ChatGptTopBar(
                        selectedModel = currentModel,
                        isTemporaryChat = isTemporaryChat,
                        onModelSelected = { viewModel.selectModel(it) },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        onNewChat = {
                            viewModel.createNewChat()
                            activeTab = 3
                        },
                        onOpenVoiceMode = {
                            if (hasMicPermission) {
                                viewModel.openVoiceMode()
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        onToggleTools = {
                            activeTab = if (activeTab == 3) 0 else 3
                        },
                        activeTab = activeTab
                    )

                if (showSettings) {
                    SettingsPanel(
                        onClose = { showSettings = false },
                        onClearAllMemories = { viewModel.clearAllMemories() },
                        onRequestSmsPermission = { smsPermissionLauncher.launch(Manifest.permission.SEND_SMS) },
                        onRequestContactsPermission = { contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS) },
                        hasSmsPermission = hasSmsPermission,
                        hasContactsPermission = hasContactsPermission
                    )
                } else {
                    // 2. Main Dashboard & Assistant Workspace
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        AnimatedVisibility(visible = hasRecoveryPending) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0x33FFB4AB)),
                                border = BorderStroke(1.dp, Color(0xFFFFB4AB))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Warning",
                                            tint = Color(0xFFFFB4AB),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "क्रैश रिकवरी (Crash Recovery Alert)",
                                            color = Color(0xFFFFB4AB),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Rohit AI ने एक बाधित सत्र (interrupted session) का पता लगाया है। क्या आप पिछली बची हुई बहु-चरण (multi-step) क्रियाओं को फिर से शुरू करना चाहते हैं या हटाना चाहते हैं?",
                                        color = Color.LightGray,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(onClick = { viewModel.discardCrashedSession() }) {
                                            Text("DISCARD (हटाएं)", color = Color(0xFFFFB4AB), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            onClick = { viewModel.restoreCrashedSession() },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB4AB)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("RESTORE (बहाल करें)", color = Color(0xFF561D24), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Section Tab Bar
                        ScrollableTabRow(
                            selectedTabIndex = activeTab,
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFFD0BCFF),
                            edgePadding = 0.dp,
                            divider = {},
                            indicator = {}
                        ) {
                            val tabs = listOf("🏠 Smart Home", "📋 Reminders", "🧠 Memory", "💬 Chat Log", "🛡️ Governance", "🎯 Certification")

                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = activeTab == index,
                                    onClick = { activeTab = index },
                                    modifier = Modifier
                                        .padding(vertical = 8.dp, horizontal = 4.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (activeTab == index) Color(0xFF3F326D) else Color(0x1AFFFFFF)
                                        ),
                                    text = {
                                        Text(
                                            text = title,
                                            fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Normal,
                                            color = if (activeTab == index) Color.White else Color(0xFFCCC2DC),
                                            fontSize = 13.sp
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Current Tab content
                        Box(modifier = Modifier.weight(1f)) {
                            when (activeTab) {
                                0 -> SmartHomeGrid(
                                    devices = devices,
                                    onToggleDevice = { viewModel.toggleDeviceDirectly(it) }
                                )
                                1 -> RemindersTab(
                                    reminders = reminders,
                                    onToggleComplete = { viewModel.toggleReminderCompletion(it) },
                                    onDelete = { viewModel.deleteReminderDirectly(it) },
                                    onAdd = { viewModel.addReminderDirectly(it) }
                                )
                                2 -> MemoriesTab(
                                    memories = memories,
                                    onDelete = { viewModel.deleteMemoryDirectly(it) },
                                    onAdd = { viewModel.addMemoryDirectly(it) }
                                )
                                3 -> ChatHistoryView(
                                    messages = sessionMessages,
                                    listState = listState,
                                    onRateMessage = { id, rating -> viewModel.rateMessage(id, rating) },
                                    onRegenerate = { viewModel.regenerateLastResponse() },
                                    onSpeak = { text -> viewModel.speak(text) },
                                    onEditPrompt = { id, text -> viewModel.editPromptAndResubmit(id, text) },
                                    onDeleteMessage = { id -> viewModel.deleteMessage(id) },
                                    onPromptSuggestionClicked = { prompt -> viewModel.processUserQuery(prompt) }
                                )
                                4 -> GovernanceTab(
                                    viewModel = viewModel
                                )
                                5 -> CertificationTab(
                                    viewModel = viewModel
                                )
                            }

                        }
                    }
                }

                // Error feedback box (if any)
                AnimatedVisibility(visible = errorMessage != null) {
                    errorMessage?.let {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF3A1A22))
                                .border(1.dp, Color(0xFFFFB4AB), RoundedCornerShape(12.dp))
                                .clickable { viewModel.clearErrorMessage() }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = it,
                                    color = Color(0xFFFFDAD6),
                                    fontSize = 13.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss error",
                                    tint = Color(0xFFFFB4AB),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // 3. Interactive Input & Voice Controller Area
                if (activeTab == 3) {
                    ChatGptBottomInputBar(
                        typedQuery = typedQuery,
                        onQueryChanged = { typedQuery = it },
                        onSend = { query ->
                            viewModel.processUserQuery(query)
                            typedQuery = ""
                        },
                        isThinking = isThinking,
                        isWebSearchActive = isWebSearchActive,
                        onToggleWebSearch = { viewModel.toggleWebSearch() },
                        onOpenVoiceMode = {
                            if (hasMicPermission) {
                                viewModel.openVoiceMode()
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        onStopStreaming = { viewModel.stopSpeaking() }
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x33000000))
                            .padding(bottom = 16.dp, top = 8.dp)
                    ) {
                    // Interim speech representation
                    if (isListening || interimText.isNotEmpty()) {
                        Text(
                            text = interimText.ifEmpty { "Say something..." },
                            color = Color(0xFFD0BCFF),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else if (isThinking) {
                        Text(
                            text = "Rohit is reasoning...",
                            color = Color(0xFFCCC2DC),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        )
                    }

                    // Modes Control Panel: Wake Word and Continuous Conversation
                    val isWakeWordActive by viewModel.isWakeWordModeActive.collectAsState()
                    val isContinuousActive by viewModel.isContinuousModeActive.collectAsState()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Wake Word Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isWakeWordActive) Color(0x3380DEEA) else Color(0x11FFFFFF))
                                .border(
                                    width = 1.dp,
                                    color = if (isWakeWordActive) Color(0xFF80DEEA) else Color(0x33FFFFFF),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    if (hasMicPermission) {
                                        viewModel.toggleWakeWordMode()
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("rohit_wake_word_toggle"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val infiniteTransition = rememberInfiniteTransition(label = "pulseWake")
                                val pulseAlpha by infiniteTransition.animateFloat(
                                    initialValue = 0.4f,
                                    targetValue = 1.0f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(1000, easing = LinearEasing),
                                        repeatMode = RepeatMode.Reverse
                                    ), label = "pulse"
                                )
                                
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isWakeWordActive) Color(0xFF80DEEA).copy(alpha = pulseAlpha)
                                            else Color(0xFFCCC2DC)
                                        )
                                )
                                Text(
                                    text = if (isWakeWordActive) "रोहित Listening" else "Wake Word: Rohit",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isWakeWordActive) Color(0xFF80DEEA) else Color(0xFFCCC2DC)
                                )
                            }
                        }

                        // Continuous Conversation Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isContinuousActive) Color(0x33A78BFA) else Color(0x11FFFFFF))
                                .border(
                                    width = 1.dp,
                                    color = if (isContinuousActive) Color(0xFFA78BFA) else Color(0x33FFFFFF),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    if (hasMicPermission) {
                                        viewModel.toggleContinuousMode()
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("continuous_mode_toggle"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val infiniteTransition = rememberInfiniteTransition(label = "pulseCont")
                                val pulseAlpha by infiniteTransition.animateFloat(
                                    initialValue = 0.4f,
                                    targetValue = 1.0f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(1000, easing = LinearEasing),
                                        repeatMode = RepeatMode.Reverse
                                    ), label = "pulse"
                                )
                                
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isContinuousActive) Color(0xFFA78BFA).copy(alpha = pulseAlpha)
                                            else Color(0xFFCCC2DC)
                                        )
                                )
                                Text(
                                    text = if (isContinuousActive) "Continuous On" else "Continuous Mode",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isContinuousActive) Color(0xFFA78BFA) else Color(0xFFCCC2DC)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    AnimatedVisibility(visible = pendingActions != null && pendingActions!!.isNotEmpty()) {
                        ProposedActionsReviewPanel(
                            actions = pendingActions ?: emptyList(),
                            onToggle = { action ->
                                viewModel.updateProposedAction(action.copy(isEnabled = !action.isEnabled))
                            },
                            onRemove = { id ->
                                viewModel.removeProposedAction(id)
                            },
                            onMoveUp = { id ->
                                viewModel.moveProposedActionUp(id)
                            },
                            onMoveDown = { id ->
                                viewModel.moveProposedActionDown(id)
                            },
                            onEditParam = { action, key, value ->
                                val updatedParams = action.editedParams.toMutableMap()
                                updatedParams[key] = value
                                viewModel.updateProposedAction(action.copy(editedParams = updatedParams))
                            },
                            onSelectContact = { action, contact ->
                                val updatedParams = action.editedParams.toMutableMap()
                                updatedParams["recipient"] = contact.phoneNumber
                                viewModel.updateProposedAction(
                                    action.copy(
                                        selectedContact = contact,
                                        editedParams = updatedParams
                                    )
                                )
                            },
                            onConfirm = {
                                viewModel.executeConfirmedActions()
                            },
                            onCancel = {
                                viewModel.setPendingActions(null)
                            }
                        )
                    }

                    AnimatedVisibility(visible = agentSteps.isNotEmpty()) {
                        AgentWorkflowPanel(steps = agentSteps)
                    }

                    // Large animated siri-waveform/orb layer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        SiriVoiceOrb(
                            isListening = isListening,
                            isThinking = isThinking,
                            isSpeaking = isSpeaking
                        )

                        // Glowing Mic Button over the waveform
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (isListening) {
                                        viewModel.stopListening()
                                    } else {
                                        if (hasMicPermission) {
                                            viewModel.startListening()
                                        } else {
                                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        }
                                    }
                                }
                                .drawBehind {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFF4F378B),
                                                Color(0x334F378B),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                }
                                .border(
                                    2.dp,
                                    if (isListening) Color(0xFFEFB8C8) else Color(0xFFD0BCFF),
                                    CircleShape
                                )
                                .background(Color(0xFF221C35)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = "Voice assistant mic toggle",
                                tint = if (isListening) Color(0xFFEFB8C8) else Color.White,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("voice_assistant_mic_toggle")
                            )
                        }
                    }

                    // Toggles (Keyboard mode / typed prompts)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { useKeyboardMode = !useKeyboardMode },
                            modifier = Modifier.testTag("toggle_keyboard_mode")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Keyboard,
                                contentDescription = "Type query instead",
                                tint = if (useKeyboardMode) Color(0xFFD0BCFF) else Color(0xFFCCC2DC)
                            )
                        }

                        if (useKeyboardMode) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = typedQuery,
                                    onValueChange = { typedQuery = it },
                                    placeholder = { Text("Ask Rohit anything...", color = Color(0x99FFFFFF)) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .testTag("text_input_query"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFD0BCFF),
                                        unfocusedBorderColor = Color(0x33FFFFFF),
                                        focusedContainerColor = Color(0x1AFFFFFF),
                                        unfocusedContainerColor = Color(0x0DFFFFFF),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(26.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                    keyboardActions = KeyboardActions(onSend = {
                                        if (typedQuery.trim().isNotEmpty()) {
                                            viewModel.processUserQuery(typedQuery)
                                            typedQuery = ""
                                            activeTab = 3 // Switch to chat view to see reply
                                        }
                                    })
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        if (typedQuery.trim().isNotEmpty()) {
                                            viewModel.processUserQuery(typedQuery)
                                            typedQuery = ""
                                            activeTab = 3 // Switch to chat view
                                        }
                                    },
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(Color(0xFF3F326D), CircleShape)
                                        .testTag("submit_query_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Send text query",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = if (isListening) "TAP TO STOP" else "TAP TO SPEAK",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFCCC2DC),
                                letterSpacing = 1.sp,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }

                        IconButton(
                            onClick = {
                                if (isSpeaking) {
                                    viewModel.stopSpeaking()
                                } else {
                                    viewModel.speak("नमस्ते, मैं आपका AI सहायक रोहित हूँ। मैं आपकी क्या मदद कर सकता हूँ?")
                                }
                            },
                            modifier = Modifier.testTag("read_aloud_toggle")
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.VolumeUp,
                                contentDescription = "Test speech sound output",
                                tint = if (isSpeaking) Color(0xFFEFB8C8) else Color(0xFFCCC2DC)
                            )
                        }
                    }
                }
            }
                }
            }
        }
    }

    // ChatGPT Voice Mode Animated Glowing Orb Dialog
    ChatGptVoiceModeDialog(
        isOpen = isVoiceModeOpen,
        isListening = isListening,
        isSpeaking = isSpeaking,
        interimText = interimText,
        selectedVoice = selectedVoice,
        onClose = { viewModel.closeVoiceMode() },
        onToggleMute = {
            if (isListening) {
                viewModel.stopListening()
            } else {
                viewModel.startListening()
            }
        }
    )

    // ChatGPT Custom Instructions Dialog
    ChatGptCustomInstructionsDialog(
        isOpen = showCustomInstructionsDialog,
        initialUserFact = customInstructionsUser,
        initialResponseStyle = customInstructionsResponse,
        onSave = { userFact, responseStyle ->
            viewModel.updateCustomInstructions(userFact, responseStyle)
        },
        onClose = { showCustomInstructionsDialog = false }
    )

    // ChatGPT Settings Dialog (Voice, Persona, Clear history)
    ChatGptSettingsDialog(
        isOpen = showChatGptSettingsDialog,
        selectedVoice = selectedVoice,
        selectedPersona = currentPersona,
        onVoiceChanged = { viewModel.selectVoice(it) },
        onPersonaChanged = { viewModel.selectPersona(it) },
        onClearAllChats = { viewModel.clearAllChats() },
        onClose = { showChatGptSettingsDialog = false }
    )
}

// --- APP BAR ---
@Composable
fun AppHeader(
    isSpeaking: Boolean,
    onClearHistory: () -> Unit,
    onToggleSettings: () -> Unit,
    isTtsReady: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Neon Pulsing Logo Indicator
            val infiniteTransition = rememberInfiniteTransition(label = "logoPulse")
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ), label = "pulse"
            )

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSpeaking) Color(0xFFEFB8C8).copy(alpha = pulseAlpha)
                        else Color(0xFFD0BCFF).copy(alpha = pulseAlpha)
                    )
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "Rohit AI Assistant",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = if (isTtsReady) "Hindi & English (Siri Engine)" else "Voice Engine Initializing...",
                    fontSize = 11.sp,
                    color = Color(0xFFCCC2DC)
                )
            }
        }

        Row {
            IconButton(
                onClick = onClearHistory,
                modifier = Modifier.testTag("clear_chat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Clear all conversation history",
                    tint = Color(0xFFCCC2DC)
                )
            }

            IconButton(
                onClick = onToggleSettings,
                modifier = Modifier.testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Voice settings",
                    tint = Color(0xFFCCC2DC)
                )
            }
        }
    }
}

// --- SIRI VOICE ORB WAVEFORM ---
@Composable
fun SiriVoiceOrb(
    isListening: Boolean,
    isThinking: Boolean,
    isSpeaking: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveTransition")

    // Rotation angle
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 4000 else 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    // Pulse size modifier
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 600 else 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    val currentScale = when {
        isListening -> pulseScale
        isThinking -> 1.05f
        isSpeaking -> pulseScale * 1.02f
        else -> 1.0f
    }

    val animatedScale by animateFloatAsState(
        targetValue = currentScale,
        animationSpec = spring(), label = "scale"
    )

    Box(
        modifier = Modifier
            .size(140.dp)
            .scale(animatedScale)
            .drawBehind {
                val brush = Brush.sweepGradient(
                    colors = if (isListening) {
                        listOf(
                            Color(0xFFEFB8C8), // Magenta glow
                            Color(0xFFD0BCFF), // Purple neon
                            Color(0xFF80DEEA), // Cyan glow
                            Color(0xFFEFB8C8)
                        )
                    } else if (isThinking) {
                        listOf(
                            Color(0xFF80DEEA),
                            Color(0xFF4F378B),
                            Color(0xFF80DEEA)
                        )
                    } else if (isSpeaking) {
                        listOf(
                            Color(0xFFEFB8C8),
                            Color(0xFFD0BCFF),
                            Color(0xFFEFB8C8)
                        )
                    } else {
                        // Sleepy idle glow
                        listOf(
                            Color(0x2A4F378B),
                            Color(0x2AD0BCFF),
                            Color(0x2A4F378B)
                        )
                    }
                )

                drawCircle(
                    brush = brush,
                    radius = size.width / 2.3f,
                    style = Stroke(
                        width = if (isListening || isThinking) 6.dp.toPx() else 3.dp.toPx()
                    )
                )

                // Draw secondary offset circle for depth
                drawCircle(
                    color = if (isListening) Color(0xFF80DEEA).copy(alpha = 0.3f) else Color.Transparent,
                    radius = size.width / 1.9f,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
    )
}

// --- SMART HOME CONTROLLER ---
@Composable
fun SmartHomeGrid(
    devices: List<SmartDevice>,
    onToggleDevice: (SmartDevice) -> Unit
) {
    Column {
        Text(
            text = "Smart Home Devices (Tools)",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(devices) { device ->
                val isActive = device.state == "on" || device.state == "unlocked" || device.state.contains("°C")
                Card(
                    modifier = Modifier
                        .size(width = 135.dp, height = 130.dp)
                        .clickable { onToggleDevice(device) }
                        .testTag("device_card_${device.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isActive) Color(0xFF3F326D) else Color(0x221C1B2F)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isActive) Color(0xFFD0BCFF) else Color(0x33FFFFFF)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (device.type) {
                                    "light" -> Icons.Default.Lightbulb
                                    "thermostat" -> Icons.Default.AcUnit
                                    "lock" -> if (device.state == "locked") Icons.Default.Lock else Icons.Default.LockOpen
                                    else -> Icons.Default.PowerSettingsNew
                                },
                                contentDescription = device.type,
                                tint = if (isActive) Color(0xFFEFB8C8) else Color(0xFFCCC2DC)
                            )

                            // Tiny glowing dot
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isActive) Color(0xFF80DEEA) else Color.Gray)
                            )
                        }

                        Column {
                            Text(
                                text = device.name,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = device.room,
                                color = Color(0xFFCCC2DC),
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = device.state.uppercase(),
                                color = if (isActive) Color(0xFF80DEEA) else Color(0xFF808080),
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// BorderStroke helper
fun BorderStroke(width: androidx.compose.ui.unit.Dp, color: Color) = androidx.compose.foundation.BorderStroke(width, color)

// --- PLANNER & REMINDERS ---
@Composable
fun RemindersTab(
    reminders: List<ReminderItem>,
    onToggleComplete: (ReminderItem) -> Unit,
    onDelete: (Int) -> Unit,
    onAdd: (String) -> Unit
) {
    var newReminderText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newReminderText,
                onValueChange = { newReminderText = it },
                placeholder = { Text("Add reminder task...", color = Color(0x66FFFFFF)) },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("add_reminder_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFD0BCFF),
                    unfocusedBorderColor = Color(0x33FFFFFF)
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (newReminderText.trim().isNotEmpty()) {
                        onAdd(newReminderText)
                        newReminderText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF3F326D), RoundedCornerShape(12.dp))
                    .testTag("add_reminder_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new task",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (reminders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No reminders yet. Ask Rohit:\n\"Remind me to call Mom today\"",
                    textAlign = TextAlign.Center,
                    color = Color(0xFFCCC2DC),
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(reminders) { reminder ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x11FFFFFF))
                            .border(1.dp, Color(0x11FFFFFF), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Checkbox(
                                checked = reminder.completed,
                                onCheckedChange = { onToggleComplete(reminder) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFFD0BCFF),
                                    uncheckedColor = Color(0xFFCCC2DC)
                                ),
                                modifier = Modifier.testTag("reminder_checkbox_${reminder.id}")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = reminder.title,
                                    color = if (reminder.completed) Color.Gray else Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    textDecoration = if (reminder.completed) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                )
                                Text(
                                    text = reminder.category.uppercase(),
                                    color = Color(0xFFEFB8C8),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(
                            onClick = { onDelete(reminder.id) },
                            modifier = Modifier.testTag("delete_reminder_${reminder.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete task",
                                tint = Color(0xFFCCC2DC),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- LONG TERM MEMORY PANEL ---
@Composable
fun MemoriesTab(
    memories: List<MemoryItem>,
    onDelete: (Int) -> Unit,
    onAdd: (String) -> Unit
) {
    var newMemoryText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newMemoryText,
                onValueChange = { newMemoryText = it },
                placeholder = { Text("Teach Rohit something about you...", color = Color(0x66FFFFFF)) },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("add_memory_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFD0BCFF),
                    unfocusedBorderColor = Color(0x33FFFFFF)
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (newMemoryText.trim().isNotEmpty()) {
                        onAdd(newMemoryText)
                        newMemoryText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF3F326D), RoundedCornerShape(12.dp))
                    .testTag("add_memory_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Teach Rohit fact",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (memories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Rohit's long term memory is empty.\nAsk Rohit:\n\"Remember that my favorite game is chess.\"",
                    textAlign = TextAlign.Center,
                    color = Color(0xFFCCC2DC),
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(memories) { memory ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x11FFFFFF))
                            .border(1.dp, Color(0x11FFFFFF), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "Memory item learned",
                                tint = Color(0xFF80DEEA),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = memory.info,
                                color = Color.White,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        IconButton(
                            onClick = { onDelete(memory.id) },
                            modifier = Modifier.testTag("delete_memory_${memory.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Forget fact",
                                tint = Color(0xFFCCC2DC),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- CONVERSATION VIEW (CHAT HISTORY) ---
@Composable
fun ChatHistoryView(
    messages: List<ConversationMessage>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onRateMessage: (Int, Int) -> Unit = { _, _ -> },
    onRegenerate: () -> Unit = {},
    onSpeak: (String) -> Unit = {},
    onEditPrompt: (Int, String) -> Unit = { _, _ -> },
    onDeleteMessage: (Int) -> Unit = {},
    onPromptSuggestionClicked: (String) -> Unit = {}
) {
    if (messages.isEmpty()) {
        ChatGptWelcomeState(
            onPromptSuggestionClicked = onPromptSuggestionClicked
        )
    } else {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val lastAssistantIndex = messages.indexOfLast { it.role != "user" }
            items(messages, key = { it.id }) { msg ->
                val isLatest = messages.indexOf(msg) == lastAssistantIndex
                ChatGptMessageItem(
                    message = msg,
                    isLatestAssistantMessage = isLatest,
                    onRateMessage = onRateMessage,
                    onRegenerate = onRegenerate,
                    onSpeak = onSpeak,
                    onEditPrompt = onEditPrompt,
                    onDeleteMessage = onDeleteMessage
                )
            }
        }
    }
}

// --- SETTINGS PANEL ---
@Composable
fun SettingsPanel(
    onClose: () -> Unit,
    onClearAllMemories: () -> Unit,
    onRequestSmsPermission: () -> Unit,
    onRequestContactsPermission: () -> Unit,
    hasSmsPermission: Boolean,
    hasContactsPermission: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("settings_card"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF221C35)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rohit Assistant Settings",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
                TextButton(onClick = onClose) {
                    Text("Close", color = Color(0xFFD0BCFF))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "System Permissions",
                color = Color(0xFFEFB8C8),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // SMS Permission Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x11FFFFFF))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Send SMS Permission", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Required for SMS action orchestration", color = Color(0xFFCCC2DC), fontSize = 10.sp)
                }
                if (hasSmsPermission) {
                    Text("GRANTED", color = Color(0xFF81C784), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                } else {
                    Button(
                        onClick = onRequestSmsPermission,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F326D)),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("GRANT", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Contacts Permission Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x11FFFFFF))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Read Contacts Lookup", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Resolves names (e.g., 'Rahul') to numbers", color = Color(0xFFCCC2DC), fontSize = 10.sp)
                }
                if (hasContactsPermission) {
                    Text("GRANTED", color = Color(0xFF81C784), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                } else {
                    Button(
                        onClick = onRequestContactsPermission,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F326D)),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("GRANT", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Voice Assistant Capabilities",
                color = Color(0xFFEFB8C8),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "• Sequential Action Orchestrator: Combines multiple steps in one request!\n" +
                        "• Action Verifier: Checks each step for permission and details.\n" +
                        "• Speech-to-Text: Auto-recognizes Hindi & English words.\n" +
                        "• Text-to-Speech: Dual language output reading.\n" +
                        "• Smart Tools: Controls lighting, ACs, locks.\n" +
                        "• App Launching: Opens YouTube, Maps, Settings.\n" +
                        "• Memory Sync: Saves customized facts about you.",
                color = Color(0xFFCCC2DC),
                fontSize = 11.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClearAllMemories,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A1A22)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("clear_all_memories_button")
            ) {
                Text("Clear All Long-Term Memories", color = Color(0xFFFFDAD6))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x11FFFFFF))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Note: Your Gemini API Key is configured via AI Studio. Never share your generated APK file publicly as API keys are packaged inside the prototype.",
                    color = Color(0xFFCCC2DC),
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AgentWorkflowPanel(steps: List<AgentStep>) {
    if (steps.isEmpty()) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("agent_orchestrator_panel"),
        colors = CardDefaults.cardColors(containerColor = Color(0xAA1F1435)),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0x66D0BCFF))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF80DEEA))
                )
                Text(
                    text = "Rohit Action Orchestrator",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "AGENT ENGINE",
                    color = Color(0xFFEFB8C8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                steps.forEach { step ->
                    val icon = when (step.action) {
                        "send_sms" -> Icons.Default.Sms
                        "update_device" -> Icons.Default.Lightbulb
                        "create_reminder" -> Icons.Default.TaskAlt
                        "open_app" -> Icons.Default.PhoneAndroid
                        "web_search" -> Icons.Default.Search
                        "save_memory" -> Icons.Default.Psychology
                        else -> Icons.Default.HourglassEmpty
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when (step.status) {
                                    StepStatus.RUNNING -> Color(0x2280DEEA)
                                    StepStatus.COMPLETED -> Color(0x1181C784)
                                    StepStatus.FAILED -> Color(0x22E57373)
                                    StepStatus.PENDING -> Color(0x08FFFFFF)
                                    StepStatus.ROLLED_BACK -> Color(0x22FFB74D)
                                }
                            )
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = when (step.status) {
                                    StepStatus.RUNNING -> Color(0xFF80DEEA)
                                    StepStatus.COMPLETED -> Color(0xFF81C784)
                                    StepStatus.FAILED -> Color(0xFFE57373)
                                    StepStatus.PENDING -> Color(0xFFCCC2DC)
                                    StepStatus.ROLLED_BACK -> Color(0xFFFFB74D)
                                },
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = step.description,
                                    color = when (step.status) {
                                        StepStatus.PENDING -> Color(0x88FFFFFF)
                                        else -> Color.White
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                if (step.status == StepStatus.FAILED && step.errorMessage != null) {
                                    Text(
                                        text = "Error: ${step.errorMessage}",
                                        color = Color(0xFFFFB4AB),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Right hand status indicator
                        when (step.status) {
                            StepStatus.PENDING -> {
                                Text(text = "WAITING", color = Color(0x44FFFFFF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            StepStatus.RUNNING -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF80DEEA)
                                )
                            }
                            StepStatus.COMPLETED -> {
                                Icon(
                                    imageVector = Icons.Default.TaskAlt,
                                    contentDescription = "Success",
                                    tint = Color(0xFF81C784),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            StepStatus.FAILED -> {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Failed",
                                    tint = Color(0xFFE57373),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            StepStatus.ROLLED_BACK -> {
                                Icon(
                                    imageVector = Icons.Default.Undo,
                                    contentDescription = "Rolled Back",
                                    tint = Color(0xFFFFB74D),
                                    modifier = Modifier.size(16.dp)
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
fun ProposedActionsReviewPanel(
    actions: List<ProposedAction>,
    onToggle: (ProposedAction) -> Unit,
    onRemove: (String) -> Unit,
    onMoveUp: (String) -> Unit,
    onMoveDown: (String) -> Unit,
    onEditParam: (ProposedAction, String, String) -> Unit,
    onSelectContact: (ProposedAction, ContactMatch) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                width = 1.dp,
                color = Color(0xFF80DEEA).copy(alpha = 0.5f),
                shape = RoundedCornerShape(24.dp)
            )
            .testTag("consent_panel_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xEE1E113A))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Review Security",
                    tint = Color(0xFF80DEEA),
                    modifier = Modifier.size(24.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Review Proposed Actions",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Authorize or customize actions before execution",
                        color = Color(0xCCFFFFFF),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Items
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                actions.forEachIndexed { index, pAction ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (pAction.isEnabled) Color(0x1A80DEEA) else Color(0x05FFFFFF)
                            )
                            .border(
                                width = 1.dp,
                                color = if (pAction.isEnabled) Color(0x3380DEEA) else Color(0x11FFFFFF),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Column {
                            // Top Row: Move, Checkbox, Icon, Title, Delete
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Reordering buttons
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (index > 0) {
                                        IconButton(
                                            onClick = { onMoveUp(pAction.id) },
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowUpward,
                                                contentDescription = "Move Up",
                                                tint = Color.White.copy(alpha = 0.6f),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    if (index < actions.size - 1) {
                                        IconButton(
                                            onClick = { onMoveDown(pAction.id) },
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowDownward,
                                                contentDescription = "Move Down",
                                                tint = Color.White.copy(alpha = 0.6f),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }

                                // Checkbox to Toggle
                                Checkbox(
                                    checked = pAction.isEnabled,
                                    onCheckedChange = { onToggle(pAction) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF80DEEA),
                                        uncheckedColor = Color.White.copy(alpha = 0.4f),
                                        checkmarkColor = Color(0xFF0D0E15)
                                    ),
                                    modifier = Modifier.size(24.dp).testTag("action_checkbox_${pAction.action}")
                                )

                                // Action Icon
                                val icon = when (pAction.action) {
                                    "send_sms" -> Icons.Default.Sms
                                    "update_device" -> Icons.Default.Lightbulb
                                    "create_reminder" -> Icons.Default.TaskAlt
                                    "open_app" -> Icons.Default.PhoneAndroid
                                    "web_search" -> Icons.Default.Search
                                    "save_memory" -> Icons.Default.Psychology
                                    else -> Icons.Default.Edit
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (pAction.isEnabled) Color(0xFF80DEEA) else Color.White.copy(alpha = 0.4f),
                                    modifier = Modifier.size(20.dp)
                                )

                                // Action Label
                                Text(
                                    text = pAction.description,
                                    color = if (pAction.isEnabled) Color.White else Color.White.copy(alpha = 0.4f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f)
                                )

                                // Delete Action Button
                                IconButton(
                                    onClick = { onRemove(pAction.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove Action",
                                        tint = Color(0xFFE57373).copy(alpha = 0.8f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Expandable Custom Parameter Editing (only visible when action is enabled)
                            if (pAction.isEnabled) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(start = 28.dp)
                                ) {
                                    when (pAction.action) {
                                        "send_sms" -> {
                                            // Recipient Input
                                            OutlinedTextField(
                                                value = pAction.editedParams["recipient"] ?: "",
                                                onValueChange = { onEditParam(pAction, "recipient", it) },
                                                label = { Text("Recipient", fontSize = 10.sp) },
                                                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp),
                                                modifier = Modifier.fillMaxWidth().testTag("sms_recipient_input"),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFF80DEEA),
                                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                                    focusedLabelColor = Color(0xFF80DEEA),
                                                    unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                                                )
                                            )

                                            // SMS Message Input
                                            OutlinedTextField(
                                                value = pAction.editedParams["message"] ?: "",
                                                onValueChange = { onEditParam(pAction, "message", it) },
                                                label = { Text("Message Body", fontSize = 10.sp) },
                                                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp),
                                                modifier = Modifier.fillMaxWidth().testTag("sms_message_input"),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFF80DEEA),
                                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                                    focusedLabelColor = Color(0xFF80DEEA),
                                                    unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                                                )
                                            )

                                            // Disambiguation / Suggested Matches Area
                                            if (pAction.availableContacts.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "Select matching contact:",
                                                    color = Color(0xFF80DEEA),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .horizontalScroll(rememberScrollState())
                                                        .padding(vertical = 4.dp),
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    pAction.availableContacts.forEach { contact ->
                                                        val isSelected = pAction.selectedContact?.phoneNumber == contact.phoneNumber
                                                        AssistChip(
                                                            onClick = { onSelectContact(pAction, contact) },
                                                            label = {
                                                                Text(
                                                                    text = "${contact.name} (${contact.phoneNumber})",
                                                                    fontSize = 10.sp,
                                                                    color = if (isSelected) Color(0xFF0D0E15) else Color.White
                                                                )
                                                            },
                                                            colors = AssistChipDefaults.assistChipColors(
                                                                containerColor = if (isSelected) Color(0xFF80DEEA) else Color(0x22FFFFFF),
                                                                labelColor = if (isSelected) Color(0xFF0D0E15) else Color.White
                                                            ),
                                                            border = BorderStroke(
                                                                width = 1.dp,
                                                                color = if (isSelected) Color(0xFF80DEEA) else Color.White.copy(alpha = 0.2f)
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        "create_reminder" -> {
                                            OutlinedTextField(
                                                value = pAction.editedParams["title"] ?: "",
                                                onValueChange = { onEditParam(pAction, "title", it) },
                                                label = { Text("Reminder Title", fontSize = 10.sp) },
                                                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp),
                                                modifier = Modifier.fillMaxWidth().testTag("reminder_title_input"),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFF80DEEA),
                                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                                    focusedLabelColor = Color(0xFF80DEEA),
                                                    unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                                                )
                                            )
                                        }
                                        "web_search" -> {
                                            OutlinedTextField(
                                                value = pAction.editedParams["query"] ?: "",
                                                onValueChange = { onEditParam(pAction, "query", it) },
                                                label = { Text("Search Query", fontSize = 10.sp) },
                                                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp),
                                                modifier = Modifier.fillMaxWidth().testTag("search_query_input"),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFF80DEEA),
                                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                                    focusedLabelColor = Color(0xFF80DEEA),
                                                    unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                                                )
                                            )
                                        }
                                        "save_memory" -> {
                                            OutlinedTextField(
                                                value = pAction.editedParams["fact"] ?: "",
                                                onValueChange = { onEditParam(pAction, "fact", it) },
                                                label = { Text("Fact to Remember", fontSize = 10.sp) },
                                                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp),
                                                modifier = Modifier.fillMaxWidth().testTag("memory_fact_input"),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFF80DEEA),
                                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                                    focusedLabelColor = Color(0xFF80DEEA),
                                                    unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
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

            Spacer(modifier = Modifier.height(16.dp))

            // Action Confirmation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f).testTag("consent_cancel_button"),
                    border = BorderStroke(1.dp, Color(0xFFE57373)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE57373))
                ) {
                    Text("Cancel", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                val enabledCount = actions.count { it.isEnabled }
                Button(
                    onClick = onConfirm,
                    enabled = enabledCount > 0,
                    modifier = Modifier.weight(1.5f).testTag("consent_confirm_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF80DEEA),
                        contentColor = Color(0xFF0D0E15)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (enabledCount > 0) "Execute ($enabledCount)" else "Select Action",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GovernanceTab(
    viewModel: AssistantViewModel,
    modifier: Modifier = Modifier
) {
    val policies by viewModel.policies.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val dryRunMode by viewModel.dryRunMode.collectAsState()
    val simulatedConfidence by viewModel.simulatedConfidence.collectAsState()
    val isTtsReady by viewModel.isTtsReady.collectAsState()
    val isEmergencyStopped by viewModel.isEmergencyStopped.collectAsState()
    val sandboxTimeoutMs by viewModel.sandboxTimeoutMs.collectAsState()
    val activeSessionId by viewModel.activeSessionId.collectAsState()
    val permissionsState by viewModel.permissionsState.collectAsState()
    val toolReputations by viewModel.toolReputations.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 0. EMERGENCY STOP & SESSION CONTEXT CONTROLLER
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isEmergencyStopped) Color(0x33E57373) else Color(0x15D32F2F)
                ),
                border = BorderStroke(1.5.dp, if (isEmergencyStopped) Color(0xFFE57373) else Color(0xFFD32F2F).copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🚨 SYSTEM EMERGENCY STOP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE57373),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (activeSessionId.isNotEmpty()) "Active Session: $activeSessionId" else "System Status: Idle Monitoring",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        if (isEmergencyStopped) {
                            Text(
                                text = "🛑 ABORTED",
                                color = Color(0xFFE57373),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier
                                    .background(Color(0x33E57373), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        } else {
                            Text(
                                text = "🛡️ SECURE",
                                color = Color(0xFF81C784),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(Color(0x1181C784), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "विपत्तिजनक स्थितियों में चल रहे सभी AI एजेंट क्रियाकलापों को तुरंत रद्द करें और डेटा को उसकी पुरानी सुरक्षित स्थिति में वापस लाएं (Auto-Rollback)।",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.triggerEmergencyStop() },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isEmergencyStopped
                        ) {
                            Icon(Icons.Default.Security, contentDescription = "Emergency Stop", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ABORT ALL ACTIONS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        
                        if (isEmergencyStopped) {
                            OutlinedButton(
                                onClick = { viewModel.resetEmergencyStop() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Undo, contentDescription = "Reset Emergency Stop", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("RESET HALT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
 
        // 1. System Capabilities Engine
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x11FFFFFF)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚙️ DEVICE CAPABILITY ENGINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF80DEEA),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CapabilityBadge(name = "📶 Net Connection", isOk = true)
                        CapabilityBadge(name = "💬 Speech Voice", isOk = isTtsReady)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CapabilityBadge(name = "🎤 STT Engine", isOk = true)
                        CapabilityBadge(name = "🛡️ Safety Policy", isOk = true)
                    }
                }
            }
        }
 
        // 2. RUNTIME PERMISSION ENGINE
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x11FFFFFF)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔑 RUNTIME PERMISSION ENGINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF80DEEA),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "सुरक्षा उल्लंघन रोकने के लिए टूल चलाने से पहले आवश्यक परमिशन की जाँच करें। टेस्ट करने के लिए इन्हें टॉगल करें:",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    permissionsState.forEach { (permission, isGranted) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = when (permission) {
                                        "SEND_SMS" -> "📤 SEND_SMS (संदेश भेजना)"
                                        "READ_CONTACTS" -> "📖 READ_CONTACTS (संपर्क पढ़ना)"
                                        "LOCATION" -> "📍 ACCESS_LOCATION (स्थान)"
                                        "BACKGROUND_EXECUTION" -> "🔄 BACKGROUND_SERVICE (पृष्ठभूमि निष्पादन)"
                                        "BATTERY_OPTIMIZATION_DISABLED" -> "⚡ BATTERY_OPTIMIZATION (बैटरी सीमा हटाना)"
                                        else -> permission
                                    },
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Switch(
                                checked = isGranted,
                                onCheckedChange = { viewModel.togglePermission(permission, it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF81C784),
                                    checkedTrackColor = Color(0xFF1B5E20)
                                )
                            )
                        }
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    }
                }
            }
        }
 
        // 3. TOOL SANDBOX CONTROLLER
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x11FFFFFF)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📦 TOOL SANDBOX EXECUTION CONTROLS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF80DEEA),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Tool Maximum Execution Timeout: ${sandboxTimeoutMs}ms",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "यदि कोई टूल इस समय सीमा से अधिक समय लेता है, तो सैंडबॉक्स उसे निरस्त (Abort) कर देगा।",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = sandboxTimeoutMs.toFloat(),
                        onValueChange = { viewModel.setSandboxTimeout(it.toInt()) },
                        valueRange = 1000f..10000f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF80DEEA),
                            activeTrackColor = Color(0xFF80DEEA)
                        )
                    )
                }
            }
        }
 
        // 4. Policy Engine Configuration Rules (with Priority labels)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x11FFFFFF)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🛡️ SECURITY POLICY ENGINE & RESOLUTION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF80DEEA),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    policies.forEach { rule ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = rule.title,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    
                                    val priorityColor = when (rule.priority) {
                                        "SYSTEM" -> Color(0xFFE57373)
                                        "SECURITY" -> Color(0xFFFFB74D)
                                        "USER" -> Color(0xFF64B5F6)
                                        "SESSION" -> Color(0xFF81C784)
                                        else -> Color.White
                                    }
                                    Text(
                                        text = rule.priority,
                                        color = priorityColor,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .border(1.dp, priorityColor.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = rule.description,
                                    color = Color.LightGray,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Switch(
                                checked = rule.isEnabled,
                                onCheckedChange = { viewModel.togglePolicy(rule.id, it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF80DEEA),
                                    checkedTrackColor = Color(0xFF3F326D)
                                )
                            )
                        }
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    }
                }
            }
        }
 
        // 5. TOOL HEALTH & REPUTATION ENGINE
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x11FFFFFF)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📊 TOOL HEALTH & REPUTATION INDEX",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF80DEEA),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (toolReputations.isEmpty()) {
                        Text(
                            text = "No tools run in this session. Running tools generates real-time reputation analysis.",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        toolReputations.forEach { rep ->
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = rep.toolName.uppercase(),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${rep.successRate}% Success Rate",
                                        color = if (rep.successRate >= 90) Color(0xFF81C784) else if (rep.successRate >= 50) Color(0xFFFFB74D) else Color(0xFFE57373),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Total Calls: ${rep.totalCalls} | Failures: ${rep.failureCalls}",
                                        color = Color.LightGray,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "Avg Time: ${rep.avgExecutionTime} ms",
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                            }
                        }
                    }
                }
            }
        }
 
        // 6. Dry Run Simulation Controller
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x15140F26)),
                border = BorderStroke(1.dp, Color(0xFF80DEEA).copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🧪 DRY RUN & CONFIDENCE SIMULATOR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF80DEEA),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Dry Run / Simulation Mode",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "हमेशा कार्य करने से पहले पूरा प्लान दिखाएँ",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = dryRunMode,
                            onCheckedChange = { viewModel.setDryRunMode(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF80DEEA),
                                checkedTrackColor = Color(0xFF3F326D)
                            )
                        )
                    }
 
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    Spacer(modifier = Modifier.height(12.dp))
 
                    Text(
                        text = "Simulated Intent Confidence: $simulatedConfidence%",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "80% से कम होने पर कॉन्फिडेंस की कमी की पुष्टि मांगेगा",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Slider(
                        value = simulatedConfidence.toFloat(),
                        onValueChange = { viewModel.setSimulatedConfidence(it.toInt()) },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF80DEEA),
                            activeTrackColor = Color(0xFF80DEEA)
                        )
                    )
                }
            }
        }
 
        // 7. Detailed Audit Trail Logs
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x11FFFFFF)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📋 AGENT AUDIT TRAIL LOGS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF80DEEA),
                            letterSpacing = 1.sp
                        )
                        if (auditLogs.isNotEmpty()) {
                            Text(
                                text = "Clear Logs",
                                color = Color(0xFFFF8A65),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { viewModel.clearAuditLogs() }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
 
                    if (auditLogs.isEmpty()) {
                        Text(
                            text = "No actions executed yet. Try talking to the assistant!",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        auditLogs.forEach { log ->
                            AuditLogItemView(log = log)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuditLogItemView(log: com.example.database.AuditLog) {
    var expandedChain by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x18FFFFFF))
            .border(1.dp, Color.White.copy(alpha = 0.07f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = log.toolName.uppercase(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                RiskBadge(risk = log.riskLevel)
                
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = log.sessionId,
                    color = Color(0xFF80DEEA),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color(0x2280DEEA), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
            
            val formattedTime = java.text.SimpleDateFormat("hh:mm:ss a", java.util.Locale.getDefault()).format(java.util.Date(log.timestamp))
            Text(
                text = formattedTime,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 10.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Params: ${log.parameters}",
            color = Color.LightGray,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val statusColor = when (log.status) {
                    "SUCCESS" -> Color(0xFF81C784)
                    "FAILED" -> Color(0xFFE57373)
                    "ROLLED_BACK" -> Color(0xFFFFB74D)
                    else -> Color.White
                }
                val statusText = when (log.status) {
                    "SUCCESS" -> "✅ SUCCESS"
                    "FAILED" -> "❌ FAILED"
                    "ROLLED_BACK" -> "🛡️ ROLLED BACK"
                    else -> log.status
                }
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                val permColor = if (log.permissionStatus == "DENIED") Color(0xFFE57373) else Color(0xFF81C784)
                Text(
                    text = "Perm: ${log.permissionStatus ?: "GRANTED"}",
                    color = permColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )

                if (log.rollbackSupported && log.status == "SUCCESS") {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(Rollback Ready)",
                        color = Color(0xFF80DEEA),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Text(
                text = "${log.executionTimeMs} ms",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
        }
        if (log.errorMessage != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Error: ${log.errorMessage}",
                color = Color(0xFFFFDAD6),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Forensic Autopsy Chain Toggle Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0x2280DEEA))
                .clickable { expandedChain = !expandedChain }
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔬", fontSize = 11.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (expandedChain) "Hide Forensic Autopsy Chain" else "View Forensic Decision Chain (11 Nodes)",
                    color = Color(0xFF80DEEA),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = if (expandedChain) "▲" else "▼",
                color = Color(0xFF80DEEA),
                fontSize = 9.sp
            )
        }

        // Expandable Forensic Chain Visualization
        AnimatedVisibility(visible = expandedChain) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F0B1E))
                    .border(1.dp, Color(0xFF80DEEA).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "IMMUTABLE FORENSIC DECISION AUDIT",
                    color = Color(0xFF80DEEA),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                val rollbackStateInfo = when {
                    log.rollbackDetails == "PARTIAL" -> Pair("PARTIAL_RESTORE", Color(0xFFFFB74D))
                    log.rollbackDetails == "FAILED" || (log.status == "FAILED" && log.errorMessage?.contains("Rollback", true) == true) -> Pair("CASCADE_FAILED", Color(0xFFE57373))
                    log.status == "ROLLED_BACK" || log.rollbackDetails == "RESTORED" -> Pair("CASCADE_RESTORED", Color(0xFF81C784))
                    log.rollbackSupported && log.status == "SUCCESS" -> Pair("AVAILABLE", Color(0xFF80DEEA))
                    else -> Pair("NOT_APPLICABLE", Color.Gray)
                }

                val chainNodes = listOf(
                    Triple("1. SESSION", log.sessionId, Color(0xFF80DEEA)),
                    Triple("2. USER INTENT", log.userIntent ?: "Natural voice/UI instruction", Color.White),
                    Triple("3. LLM PLAN", log.llmPlan ?: "Tool: ${log.toolName} with arguments", Color(0xFFD0BCFF)),
                    Triple("4. CONFIDENCE", "${(log.confidenceScore * 100).toInt().coerceAtLeast(85)}%", Color(0xFF81C784)),
                    Triple("5. POLICY DECISION", log.policyDecision ?: "ALLOWED", if (log.status == "FAILED" && log.errorMessage?.contains("Policy", true) == true) Color(0xFFE57373) else Color(0xFF81C784)),
                    Triple("6. RISK LEVEL", log.riskLevel, when (log.riskLevel) { "HIGH", "CRITICAL" -> Color(0xFFE57373); "MEDIUM" -> Color(0xFFFFB74D); else -> Color(0xFF81C784) }),
                    Triple("7. CAPABILITY CHECK", log.capabilityCheck ?: "DEVICE_ONLINE", Color(0xFF81C784)),
                    Triple("8. PERMISSION CHECK", log.permissionStatus ?: "GRANTED", if (log.permissionStatus == "DENIED") Color(0xFFE57373) else Color(0xFF81C784)),
                    Triple("9. USER CONSENT", if (log.userConfirmation) "USER_CONFIRMED" else "NOT_REQUIRED", Color(0xFF80DEEA)),
                    Triple("10. EXECUTION", "${log.status} (${log.executionTimeMs} ms)", if (log.status == "SUCCESS") Color(0xFF81C784) else Color(0xFFE57373)),
                    Triple("11. ROLLBACK", rollbackStateInfo.first, rollbackStateInfo.second)
                )

                chainNodes.forEachIndexed { idx, (label, value, color) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            color = Color.Gray,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = value,
                            color = color,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                    if (idx < chainNodes.size - 1) {
                        HorizontalDivider(color = Color.White.copy(alpha = 0.04f), modifier = Modifier.padding(vertical = 1.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun CapabilityBadge(name: String, isOk: Boolean) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isOk) Color(0x1181C784) else Color(0x11E57373))
            .border(1.dp, if (isOk) Color(0x3381C784) else Color(0x33E57373), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (isOk) Color(0xFF81C784) else Color(0xFFE57373))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun RiskBadge(risk: String) {
    val (color, text) = when (risk.uppercase()) {
        "LOW" -> Color(0xFF81C784) to "LOW RISK"
        "MEDIUM" -> Color(0xFFFFD54F) to "MED RISK"
        "HIGH" -> Color(0xFFFF8A65) to "HIGH RISK"
        "CRITICAL" -> Color(0xFFE57373) to "CRITICAL"
        else -> Color.White to risk
    }
    Text(
        text = text,
        color = color,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp, vertical = 1.dp)
    )
}

@Composable
fun CertificationTab(viewModel: AssistantViewModel) {
    val goldenMissions by viewModel.goldenMissions.collectAsState()
    val certifications by viewModel.certifications.collectAsState()

    val latestCert = certifications.firstOrNull()
    val certifiedCount = certifications.count { it.status == "CERTIFIED" }
    val avgScore = if (certifications.isNotEmpty()) {
        certifications.map { it.totalScore }.average().toInt()
    } else 0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Mission Certification Status Shield Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("certification_shield_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1538)),
                border = BorderStroke(
                    1.dp,
                    when (latestCert?.status) {
                        "CERTIFIED" -> Color(0xFF81C784)
                        "UNCERTIFIED" -> Color(0xFFFFD54F)
                        "FAILED" -> Color(0xFFE57373)
                        else -> Color(0xFF80DEEA)
                    }
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎯", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Jarvis Mission Certification",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Golden Dataset & Evidence Validation",
                                    color = Color(0xFFCCC2DC),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        val statusText = latestCert?.status ?: "READY"
                        val statusBg = when (statusText) {
                            "CERTIFIED" -> Color(0x3381C784)
                            "UNCERTIFIED" -> Color(0x33FFD54F)
                            "FAILED" -> Color(0x33E57373)
                            else -> Color(0x3380DEEA)
                        }
                        val statusBorder = when (statusText) {
                            "CERTIFIED" -> Color(0xFF81C784)
                            "UNCERTIFIED" -> Color(0xFFFFD54F)
                            "FAILED" -> Color(0xFFE57373)
                            else -> Color(0xFF80DEEA)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(statusBg)
                                .border(1.dp, statusBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "🛡️ $statusText",
                                color = statusBorder,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Missions Certified", color = Color.Gray, fontSize = 11.sp)
                            Text("$certifiedCount / ${certifications.size}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Column {
                            Text("Avg Match Score", color = Color.Gray, fontSize = 11.sp)
                            Text("$avgScore%", color = Color(0xFF81C784), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Column {
                            Text("Golden Benchmarks", color = Color.Gray, fontSize = 11.sp)
                            Text("${goldenMissions.size} Standards", color = Color(0xFF80DEEA), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.runGoldenBenchmarkSuite() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("run_benchmark_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F52FF)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("🚀 Run Golden Benchmark", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.clearAllCertifications() },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                        ) {
                            Text("🗑️ Clear", color = Color.LightGray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 2. Latest Certification Detail Card
        if (latestCert != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF140F26)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Latest Certification Evaluation",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Query: \"${latestCert?.userQuery}\"",
                            color = Color(0xFFCCC2DC),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Planning Validator", color = Color.Gray, fontSize = 11.sp)
                                Text("${latestCert?.planningScore?.toInt()}% Match", color = Color(0xFF81C784), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Column {
                                Text("Execution Validator", color = Color.Gray, fontSize = 11.sp)
                                Text("${latestCert?.executionScore?.toInt()}% Anchor Coverage", color = Color(0xFF80DEEA), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = latestCert?.certificationNotes ?: "",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 3. Golden Dataset Standards Catalog Header
        item {
            Text(
                text = "Golden Dataset Standards (${goldenMissions.size})",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(goldenMissions.size) { index ->
            val mission = goldenMissions[index]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x22FFFFFF)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${mission.id}: ${mission.title}",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF3F326D))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(mission.category, color = Color(0xFFD0BCFF), fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(mission.description, color = Color.Gray, fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sample Query: \"${mission.sampleQueries.split(",").firstOrNull() ?: ""}\"",
                        color = Color(0xFFCCC2DC),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0x2281C784))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Min Plan: ${mission.minPlanningScore.toInt()}%", color = Color(0xFF81C784), fontSize = 10.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0x2280DEEA))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Min Exec: ${mission.minExecutionScore.toInt()}%", color = Color(0xFF80DEEA), fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // 4. Mission Certification History Audit Stream
        if (certifications.isNotEmpty()) {
            item {
                Text(
                    text = "Certification Audit History (${certifications.size})",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            items(certifications.size) { idx ->
                val cert = certifications[idx]
                val statusColor = when (cert.status) {
                    "CERTIFIED" -> Color(0xFF81C784)
                    "UNCERTIFIED" -> Color(0xFFFFD54F)
                    else -> Color(0xFFE57373)
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x15FFFFFF)),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = cert.status,
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Mission: ${cert.missionId}",
                                    color = Color.Gray,
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = cert.userQuery,
                                color = Color.White,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${cert.totalScore.toInt()}% Match",
                                color = statusColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Plan: ${cert.planningScore.toInt()}% | Exec: ${cert.executionScore.toInt()}%",
                                color = Color.Gray,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

