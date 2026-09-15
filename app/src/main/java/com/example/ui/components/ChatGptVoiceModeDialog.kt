package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.ChatGptDarkBg
import com.example.ui.theme.ChatGptEmerald

@Composable
fun ChatGptVoiceModeDialog(
    isOpen: Boolean,
    isListening: Boolean,
    isSpeaking: Boolean,
    interimText: String,
    selectedVoice: String,
    onClose: () -> Unit,
    onToggleMute: () -> Unit
) {
    if (!isOpen) return

    val infiniteTransition = rememberInfiniteTransition(label = "voice_orb")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_pulse"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb_rotation"
    )

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = ChatGptDarkBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top: Voice label & close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0x22FFFFFF),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ChatGptEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Voice: $selectedVoice",
                                fontSize = 12.sp,
                                color = Color(0xFFE0E0E0),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("close_voice_mode_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Voice Mode",
                            tint = Color.White
                        )
                    }
                }

                // Center: Iconic Pulsing Animated ChatGPT Voice Orb
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(240.dp)
                    ) {
                        // Outer glowing aura
                        Canvas(
                            modifier = Modifier
                                .size(240.dp * if (isSpeaking || isListening) pulseScale else 1f)
                        ) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = if (isSpeaking) {
                                        listOf(ChatGptEmerald.copy(alpha = 0.5f), Color.Transparent)
                                    } else if (isListening) {
                                        listOf(Color(0xFF38BDF8).copy(alpha = 0.45f), Color.Transparent)
                                    } else {
                                        listOf(Color(0xFF818CF8).copy(alpha = 0.3f), Color.Transparent)
                                    }
                                )
                            )
                        }

                        // Core dynamic sphere
                        Box(
                            modifier = Modifier
                                .size(140.dp * if (isSpeaking || isListening) pulseScale else 0.98f)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = if (isSpeaking) {
                                            listOf(ChatGptEmerald, Color(0xFF059669), Color(0xFF10B981))
                                        } else if (isListening) {
                                            listOf(Color(0xFF0284C7), Color(0xFF38BDF8), Color(0xFF0EA5E9))
                                        } else {
                                            listOf(Color(0xFF4F46E5), Color(0xFF818CF8), Color(0xFF6366F1))
                                        }
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Live Status / Transcription
                    Text(
                        text = when {
                            isSpeaking -> "ChatGPT is speaking..."
                            isListening -> if (interimText.isNotBlank()) "\"$interimText\"" else "Listening..."
                            else -> "Tap microphone to speak"
                        },
                        fontSize = 17.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                // Bottom Controls: Mute toggle and End call
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute / Unmute
                    FloatingActionButton(
                        onClick = onToggleMute,
                        containerColor = if (isListening) Color(0xFF2F2F2F) else Color(0xFFEF4444),
                        contentColor = Color.White,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                            contentDescription = if (isListening) "Mute Microphone" else "Unmute Microphone"
                        )
                    }

                    // End Session
                    FloatingActionButton(
                        onClick = onClose,
                        containerColor = Color(0xFFDC2626),
                        contentColor = Color.White,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Voice Mode"
                        )
                    }
                }
            }
        }
    }
}
