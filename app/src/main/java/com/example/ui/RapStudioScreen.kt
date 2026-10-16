package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assistant.TtsManager
import com.example.studio.BeatStyle
import com.example.studio.BeatSynthesizer
import com.example.studio.RapStudioState
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RapStudioScreen(
    beatSynthesizer: BeatSynthesizer,
    ttsManager: TtsManager,
    modifier: Modifier = Modifier
) {
    val isPlaying by beatSynthesizer.isPlaying.collectAsState()
    val currentStyle by beatSynthesizer.currentStyle.collectAsState()
    val bpm by beatSynthesizer.bpm.collectAsState()
    val activeStep by beatSynthesizer.activeStep.collectAsState()

    var rapLyrics by remember {
        mutableStateOf(
            "Mic pe aate hi macha diya shor,\nBeat pe flow mera unstoppable core!\nLala humse na takrana any more!"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Studio Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "RAP & BEAT",
                        color = StudioGold,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "STUDIO",
                        color = ElectricCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Sahi hai lala! Real-time beats aur rhyme maker",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            // Loop Play/Stop button
            Button(
                onClick = { beatSynthesizer.toggleLoop() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPlaying) StudioAmber else ElectricCyan,
                    contentColor = DarkBg
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("toggle_beat_loop_button")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Stop Beat" else "Play Beat",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isPlaying) "Stop Beat" else "Play Beat",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 1: Beat Style Selector & Sequencer
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "BEAT PRESETS",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BeatStyle.values().forEach { style ->
                        FilterChip(
                            selected = currentStyle == style,
                            onClick = { beatSynthesizer.setStyle(style) },
                            label = { Text(style.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = DarkSurfaceVariant,
                                selectedContainerColor = StudioGold,
                                labelColor = TextPrimary,
                                selectedLabelColor = DarkBg
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = currentStyle == style,
                                borderColor = if (currentStyle == style) StudioGold else DarkCardBorder
                            ),
                            modifier = Modifier.testTag("style_chip_${style.name}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 16-Step Visualizer LEDs
                Text(
                    text = "STEP SEQUENCER (${activeStep + 1}/16)",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 0 until 16) {
                        val isCurrent = isPlaying && activeStep == i
                        val isDownbeat = i % 4 == 0
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(if (isCurrent) 14.dp else 10.dp)
                                .padding(horizontal = 1.5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    when {
                                        isCurrent -> StudioGold
                                        isDownbeat -> ElectricCyan.copy(alpha = 0.5f)
                                        else -> DarkSurfaceVariant
                                    }
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // BPM Slider Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TEMPO: $bpm BPM",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { beatSynthesizer.setBpm(bpm - 5) },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Slow down", tint = TextPrimary, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { beatSynthesizer.setBpm(bpm + 5) },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Speed up", tint = TextPrimary, modifier = Modifier.size(16.dp))
                    }
                }

                Slider(
                    value = bpm.toFloat(),
                    onValueChange = { beatSynthesizer.setBpm(it.toInt()) },
                    valueRange = 60f..180f,
                    colors = SliderDefaults.colors(
                        thumbColor = StudioGold,
                        activeTrackColor = StudioGold,
                        inactiveTrackColor = DarkSurfaceVariant
                    ),
                    modifier = Modifier.testTag("bpm_slider")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 2: Interactive Drum Pads (Tappable triggers)
        Text(
            text = "LIVE DRUM PADS",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DrumPadButton(
                title = "KICK",
                subtitle = "808 Sub",
                accentColor = StudioAmber,
                onClick = { beatSynthesizer.triggerKick() },
                modifier = Modifier.weight(1f).testTag("drum_pad_kick")
            )
            DrumPadButton(
                title = "SNARE",
                subtitle = "Sharp Crack",
                accentColor = ElectricCyan,
                onClick = { beatSynthesizer.triggerSnare() },
                modifier = Modifier.weight(1f).testTag("drum_pad_snare")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DrumPadButton(
                title = "HI-HAT",
                subtitle = "Crisp Roll",
                accentColor = StudioGold,
                onClick = { beatSynthesizer.triggerHiHat() },
                modifier = Modifier.weight(1f).testTag("drum_pad_hihat")
            )
            DrumPadButton(
                title = "CLAP",
                subtitle = "Layered",
                accentColor = StudioPurple,
                onClick = { beatSynthesizer.triggerClap() },
                modifier = Modifier.weight(1f).testTag("drum_pad_clap")
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 3: Rap Lyrics & Rhyme Scratchpad
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "RAP LYRICS SCRATCHPAD",
                        color = StudioGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    AssistChip(
                        onClick = {
                            rapLyrics = RapStudioState.rapStarters.random()
                        },
                        label = { Text("Inspire Me", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = "Inspire",
                                tint = StudioAmber,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = DarkSurfaceVariant,
                            labelColor = TextPrimary
                        ),
                        border = AssistChipDefaults.assistChipBorder(true, borderColor = StudioAmber.copy(alpha = 0.5f))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = rapLyrics,
                    onValueChange = { rapLyrics = it },
                    placeholder = { Text("Apne rap ke bol yahan likhein...", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("rap_lyrics_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedBorderColor = StudioGold,
                        unfocusedBorderColor = DarkCardBorder
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Button: Ask Dharam Assistant to perform rap lyrics
                Button(
                    onClick = {
                        if (rapLyrics.isNotBlank()) {
                            ttsManager.speak("Aapun sunata hai yeh flow: $rapLyrics")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StudioGold,
                        contentColor = DarkBg
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("speak_rap_lyrics_button")
                ) {
                    Icon(Icons.Default.RecordVoiceOver, contentDescription = "Speak rap", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🎙️ Dharam Se Flow Suno (Read My Rap)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Rhyme Bank Helpers
                Text(
                    text = "DESI RHYME BANK (Tap to append word):",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                RapStudioState.sampleRhymes.forEach { group ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${group.sound}:",
                            color = ElectricCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(72.dp)
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            group.words.take(5).forEach { word ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(DarkSurfaceVariant)
                                        .border(0.5.dp, DarkCardBorder, RoundedCornerShape(6.dp))
                                        .clickable {
                                            rapLyrics = if (rapLyrics.isBlank()) word else "$rapLyrics $word"
                                        }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = word,
                                        color = TextPrimary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DrumPadButton(
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(76.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        DarkSurfaceVariant,
                        DarkSurface
                    )
                )
            )
            .border(1.5.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = accentColor),
                onClick = onClick
            )
            .padding(12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(
                text = title,
                color = accentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}
