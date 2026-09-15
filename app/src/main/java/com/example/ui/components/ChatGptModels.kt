package com.example.ui.components

data class ModelOption(
    val id: String,
    val name: String,
    val subtitle: String,
    val badge: String? = null
)

val AVAILABLE_MODELS = listOf(
    ModelOption(
        id = "GPT-4o",
        name = "GPT-4o",
        subtitle = "Great for most tasks, coding & deep reasoning",
        badge = "Smartest"
    ),
    ModelOption(
        id = "o3-mini",
        name = "o3-mini",
        subtitle = "Deep reasoning model with step-by-step thinking",
        badge = "Reasoning"
    ),
    ModelOption(
        id = "GPT-4o mini",
        name = "GPT-4o mini",
        subtitle = "Fast and lightweight for quick everyday questions",
        badge = "Fast"
    ),
    ModelOption(
        id = "SearchGPT",
        name = "SearchGPT",
        subtitle = "Real-time web browsing with factual source citations",
        badge = "Web"
    )
)

val AVAILABLE_VOICES = listOf(
    "Cove" to "Calm & Grounded",
    "Breeze" to "Upbeat & Animated",
    "Ember" to "Confident & Warm",
    "Juniper" to "Enthusiastic & Bright",
    "Sky" to "Clear & Natural",
    "Sol" to "Deep & Relaxed"
)

val AVAILABLE_PERSONAS = listOf(
    "general" to "General Assistant",
    "coder" to "Senior Software Engineer",
    "writer" to "Creative Writer & Editor",
    "tutor" to "STEM & Socratic Tutor",
    "consultant" to "Strategy & Business Consultant"
)
