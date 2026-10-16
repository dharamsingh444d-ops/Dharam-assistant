package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assistant.DharamAssistantEngine
import com.example.assistant.SpeechRecognitionManager
import com.example.assistant.TtsManager
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioGold
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun SettingsScreen(
    ttsManager: TtsManager,
    speechManager: SpeechRecognitionManager,
    engine: DharamAssistantEngine,
    modifier: Modifier = Modifier
) {
    var speechRate by remember { mutableFloatStateOf(ttsManager.speechRate) }
    var speechPitch by remember { mutableFloatStateOf(ttsManager.speechPitch) }
    var selectedLanguage by remember { mutableStateOf(ttsManager.preferredLanguage) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Settings Header
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "VOICE & ASSISTANT",
                color = StudioGold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "SETTINGS",
                color = ElectricCyan,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "Text-to-Speech engine aur voice parameters",
            color = TextMuted,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TTS Engine Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SPEECH ENGINE (pyttsx3 equivalent)",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Rate Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Speech Rate (Speed)",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${String.format(Locale.US, "%.2f", speechRate)}x",
                        color = StudioGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = speechRate,
                    onValueChange = {
                        speechRate = it
                        ttsManager.speechRate = it
                    },
                    valueRange = 0.5f..1.8f,
                    colors = SliderDefaults.colors(
                        thumbColor = StudioGold,
                        activeTrackColor = StudioGold,
                        inactiveTrackColor = DarkSurfaceVariant
                    ),
                    modifier = Modifier.testTag("speech_rate_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Pitch Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Voice Pitch",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${String.format(Locale.US, "%.2f", speechPitch)}x",
                        color = ElectricCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = speechPitch,
                    onValueChange = {
                        speechPitch = it
                        ttsManager.speechPitch = it
                    },
                    valueRange = 0.7f..1.4f,
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricCyan,
                        activeTrackColor = ElectricCyan,
                        inactiveTrackColor = DarkSurfaceVariant
                    ),
                    modifier = Modifier.testTag("speech_pitch_slider")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Language Selection
                Text(
                    text = "Preferred Language",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val languages = listOf(
                        Pair("hi-IN", "Hindi (हिन्दी)"),
                        Pair("en-IN", "English (India)"),
                        Pair("en-US", "English (US)")
                    )

                    languages.forEach { (code, label) ->
                        FilterChip(
                            selected = selectedLanguage == code,
                            onClick = {
                                selectedLanguage = code
                                ttsManager.preferredLanguage = code
                            },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = DarkSurfaceVariant,
                                selectedContainerColor = StudioGold,
                                labelColor = TextPrimary,
                                selectedLabelColor = DarkBg
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedLanguage == code,
                                borderColor = if (selectedLanguage == code) StudioGold else DarkCardBorder
                            ),
                            modifier = Modifier.testTag("lang_chip_$code")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Test voice buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            ttsManager.speak("Namaste lala! Dharam Assistant ki aawaaz ekdum zabardast chal rahi hai.")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StudioGold,
                            contentColor = DarkBg
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).testTag("test_voice_btn")
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Test Voice", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Voice", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val (greeting, followUp) = engine.getGreeting()
                            ttsManager.speak("$greeting $followUp")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkSurfaceVariant,
                            contentColor = ElectricCyan
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).testTag("wish_again_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Wish Again", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Wish Me", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Diagnostics Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "HARDWARE & SERVICE STATUS",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Active",
                        tint = SuccessGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Speech Recognition Available: ${if (speechManager.isAvailable()) "Yes (Active)" else "Limited (Use Input Fallback)"}",
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Active",
                        tint = SuccessGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Native 16-Bit AudioTrack Synthesizer: Active",
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // About & Commands reference
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = "Info", tint = StudioAmber, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VOICE COMMANDS GUIDE",
                        color = StudioAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val commandsList = listOf(
                    "⏰ 'Time / Samay / Wakt': Tells current time aloud",
                    "▶️ 'YouTube / Play': Opens YouTube or searches songs",
                    "🔍 'Google / Search': Opens Google search query",
                    "🎧 'Beat / Rap / Music': Opens studio and triggers beats",
                    "🔥 'Bars / Freestyle': Generates lyrical rap poetry",
                    "👋 'Stop / Alvida / Bye': Gracefully ends session"
                )

                commandsList.forEach { cmd ->
                    Text(
                        text = cmd,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
