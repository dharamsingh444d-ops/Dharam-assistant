package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assistant.DharamAssistantEngine
import com.example.assistant.SpeechRecognitionManager
import com.example.assistant.TtsManager
import com.example.studio.BeatSynthesizer
import com.example.ui.AssistantScreen
import com.example.ui.RapStudioScreen
import com.example.ui.SettingsScreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StudioGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

enum class AppTab(val title: String) {
    ASSISTANT("Assistant"),
    STUDIO("Rap Studio"),
    SETTINGS("Settings")
}

class MainActivity : ComponentActivity() {

    private var ttsManager: TtsManager? = null
    private var speechManager: SpeechRecognitionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val tts = TtsManager(this)
        val speech = SpeechRecognitionManager(this)
        val engine = DharamAssistantEngine()

        ttsManager = tts
        speechManager = speech

        setContent {
            MyApplicationTheme {
                val coroutineScope = rememberCoroutineScope()
                val beatSynthesizer = remember { BeatSynthesizer(coroutineScope) }
                var currentTab by remember { mutableStateOf(AppTab.ASSISTANT) }

                // Safe disposal on Activity exit
                DisposableEffect(Unit) {
                    onDispose {
                        beatSynthesizer.stopLoop()
                        tts.stop()
                    }
                }

                // Handle back press to return to main Assistant screen
                if (currentTab != AppTab.ASSISTANT) {
                    BackHandler {
                        currentTab = AppTab.ASSISTANT
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = DarkBg,
                    bottomBar = {
                        NavigationBar(
                            containerColor = DarkSurface,
                            tonalElevation = 8.dp,
                            modifier = Modifier.testTag("main_bottom_nav")
                        ) {
                            NavigationBarItem(
                                selected = currentTab == AppTab.ASSISTANT,
                                onClick = { currentTab = AppTab.ASSISTANT },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == AppTab.ASSISTANT) Icons.Filled.Mic else Icons.Outlined.Mic,
                                        contentDescription = "Assistant",
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = AppTab.ASSISTANT.title,
                                        fontWeight = if (currentTab == AppTab.ASSISTANT) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = DarkBg,
                                    selectedTextColor = StudioGold,
                                    indicatorColor = StudioGold,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_tab_assistant")
                            )

                            NavigationBarItem(
                                selected = currentTab == AppTab.STUDIO,
                                onClick = { currentTab = AppTab.STUDIO },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == AppTab.STUDIO) Icons.Filled.MusicNote else Icons.Outlined.MusicNote,
                                        contentDescription = "Rap Studio",
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = AppTab.STUDIO.title,
                                        fontWeight = if (currentTab == AppTab.STUDIO) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = DarkBg,
                                    selectedTextColor = ElectricCyan,
                                    indicatorColor = ElectricCyan,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_tab_studio")
                            )

                            NavigationBarItem(
                                selected = currentTab == AppTab.SETTINGS,
                                onClick = { currentTab = AppTab.SETTINGS },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == AppTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                        contentDescription = "Settings",
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = AppTab.SETTINGS.title,
                                        fontWeight = if (currentTab == AppTab.SETTINGS) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = DarkBg,
                                    selectedTextColor = TextPrimary,
                                    indicatorColor = TextPrimary.copy(alpha = 0.2f),
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_tab_settings")
                            )
                        }
                    }
                ) { innerPadding ->
                    when (currentTab) {
                        AppTab.ASSISTANT -> {
                            AssistantScreen(
                                engine = engine,
                                ttsManager = tts,
                                speechManager = speech,
                                onNavigateToStudio = { currentTab = AppTab.STUDIO },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        AppTab.STUDIO -> {
                            RapStudioScreen(
                                beatSynthesizer = beatSynthesizer,
                                ttsManager = tts,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        AppTab.SETTINGS -> {
                            SettingsScreen(
                                ttsManager = tts,
                                speechManager = speech,
                                engine = engine,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsManager?.shutdown()
        speechManager?.stopListening()
    }
}
