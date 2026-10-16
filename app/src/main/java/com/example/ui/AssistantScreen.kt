package com.example.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.assistant.ActionType
import com.example.assistant.AssistantMessage
import com.example.assistant.DharamAssistantEngine
import com.example.assistant.MessageSender
import com.example.assistant.SpeechRecognitionManager
import com.example.assistant.TtsManager
import com.example.ui.components.ChatBubble
import com.example.ui.components.QuickActionChips
import com.example.ui.components.VoiceOrb
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun AssistantScreen(
    engine: DharamAssistantEngine,
    ttsManager: TtsManager,
    speechManager: SpeechRecognitionManager,
    onNavigateToStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var messages by remember { mutableStateOf<List<AssistantMessage>>(emptyList()) }
    var inputText by remember { mutableStateOf("") }

    val isSpeaking by ttsManager.isSpeaking.collectAsState()
    val isListening by speechManager.isListening.collectAsState()
    val audioLevel by speechManager.audioLevel.collectAsState()
    val statusText by speechManager.statusText.collectAsState()

    // Permission launcher for RECORD_AUDIO
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            speechManager.startListening(ttsManager.preferredLanguage)
        } else {
            Toast.makeText(
                context,
                "Microphone permission is needed for voice recognition.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun handleUserQuery(query: String) {
        if (query.isBlank()) return

        // Stop any currently ongoing speech
        ttsManager.stop()

        val userMessage = AssistantMessage(
            sender = MessageSender.USER,
            text = query
        )
        messages = messages + userMessage

        // Process through Dharam Assistant Engine
        val result = engine.processQuery(query)

        val assistantMessage = AssistantMessage(
            sender = MessageSender.ASSISTANT,
            text = result.displayText,
            actionType = result.actionType,
            actionPayload = result.actionPayload
        )
        messages = messages + assistantMessage

        // Speak response out loud
        ttsManager.speak(result.speechText)

        // If action is STOP, stop speech and listening
        if (result.actionType == ActionType.STOP) {
            speechManager.stopListening()
        }

        // Auto open studio if beat command triggered
        if (result.actionType == ActionType.BEAT_STUDIO && result.actionPayload == "OPEN_STUDIO") {
            coroutineScope.launch {
                // Short pause so user can hear confirmation then navigate
                kotlinx.coroutines.delay(1200)
                onNavigateToStudio()
            }
        }
    }

    // Connect speech recognition callbacks
    LaunchedEffect(speechManager) {
        speechManager.onResultCallback = { spokenQuery ->
            handleUserQuery(spokenQuery)
        }
        speechManager.onErrorCallback = { _ ->
            // Handled in statusText
        }
    }

    // Initial greeting on first launch (matches wish_user())
    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            val (initialGreeting, followUp) = engine.getGreeting()
            val fullGreetingText = "$initialGreeting\n$followUp"
            messages = listOf(
                AssistantMessage(
                    sender = MessageSender.ASSISTANT,
                    text = fullGreetingText
                )
            )
            // Wait for TTS engine to be ready then speak
            kotlinx.coroutines.delay(800)
            ttsManager.speak(fullGreetingText)
        }
    }

    // Auto-scroll to latest message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    fun onMicClicked() {
        if (isListening) {
            speechManager.stopListening()
        } else {
            ttsManager.stop()
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            )
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                speechManager.startListening(ttsManager.preferredLanguage)
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    fun handleActionClick(actionType: ActionType, payload: String?) {
        when (actionType) {
            ActionType.YOUTUBE -> {
                val url = payload ?: "https://www.youtube.com"
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not open browser: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
            ActionType.GOOGLE -> {
                val url = payload ?: "https://www.google.com"
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not open browser: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
            ActionType.BEAT_STUDIO, ActionType.RAP_BARS -> {
                onNavigateToStudio()
            }
            else -> {}
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "DHARAM",
                        color = StudioGold,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ASSISTANT",
                        color = ElectricCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "Namaste Lala! Voice & Rap Studio",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSpeaking) {
                    IconButton(
                        onClick = { ttsManager.stop() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(StudioGold.copy(alpha = 0.2f))
                            .testTag("stop_speaking_header_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeOff,
                            contentDescription = "Stop speaking",
                            tint = StudioGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                IconButton(
                    onClick = { onNavigateToStudio() },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                        .testTag("header_studio_shortcut")
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Open Studio",
                        tint = StudioAmber,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Voice Orb Visualizer Section
        VoiceOrb(
            isListening = isListening,
            isSpeaking = isSpeaking,
            audioLevel = audioLevel,
            statusText = when {
                isListening -> statusText
                isSpeaking -> "🤖 Dharam bol raha hai..."
                else -> "Mic dabayein ya neeche bole"
            },
            onMicClick = { onMicClicked() },
            onStopSpeakingClick = { ttsManager.stop() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        // Quick prompts scrollable row
        QuickActionChips(
            onSelectPrompt = { prompt ->
                handleUserQuery(prompt)
            }
        )

        // Conversation List
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubble(
                        message = msg,
                        onSpeakClick = { text -> ttsManager.speak(text) },
                        onActionClick = { action, payload -> handleActionClick(action, payload) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Bottom Input Row
        Surface(
            color = DarkSurface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "Poochiye ya command dijiye...",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_textfield"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedBorderColor = StudioGold,
                        unfocusedBorderColor = DarkCardBorder
                    ),
                    shape = RoundedCornerShape(24.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputText.isNotBlank()) {
                                val query = inputText
                                inputText = ""
                                handleUserQuery(query)
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (inputText.isNotBlank()) {
                    IconButton(
                        onClick = {
                            val query = inputText
                            inputText = ""
                            handleUserQuery(query)
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(StudioGold)
                            .testTag("send_query_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Bhejo",
                            tint = DarkBg
                        )
                    }
                } else {
                    IconButton(
                        onClick = { onMicClicked() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (isListening) ElectricCyan else DarkSurfaceVariant)
                            .testTag("mic_inline_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Speak",
                            tint = if (isListening) DarkBg else ElectricCyan
                        )
                    }
                }
            }
        }
    }
}
