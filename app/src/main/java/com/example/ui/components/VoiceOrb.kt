package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.sin

@Composable
fun VoiceOrb(
    isListening: Boolean,
    isSpeaking: Boolean,
    audioLevel: Float,
    statusText: String,
    onMicClick: () -> Unit,
    onStopSpeakingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")

    // Pulsing animations for idle / listening / speaking
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else if (isSpeaking) 1.15f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 600 else 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = if (isListening) 0.8f else if (isSpeaking) 0.6f else 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 700 else 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_alpha"
    )

    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    val primaryAccent = when {
        isListening -> ElectricCyan
        isSpeaking -> StudioGold
        else -> StudioAmber
    }

    val secondaryAccent = when {
        isListening -> Color(0xFF0077B6)
        isSpeaking -> StudioAmber
        else -> ElectricCyan
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(190.dp)
        ) {
            // Animated Outer Halo Ring
            Canvas(modifier = Modifier.size(180.dp)) {
                val radius = size.minDimension / 2f
                val dynamicRadius = radius * (0.85f + (audioLevel * 0.15f)) * pulseScale
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryAccent.copy(alpha = haloAlpha * 0.7f),
                            secondaryAccent.copy(alpha = haloAlpha * 0.2f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = dynamicRadius
                    ),
                    radius = dynamicRadius,
                    center = center
                )

                // Neon orbit rings
                if (isListening || isSpeaking) {
                    drawCircle(
                        color = primaryAccent.copy(alpha = haloAlpha),
                        radius = radius * 0.82f * pulseScale,
                        center = center,
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
            }

            // Central Glowing Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(96.dp)
                    .scale(if (isListening) 1f + (audioLevel * 0.25f) else 1f)
                    .shadow(
                        elevation = 20.dp,
                        shape = CircleShape,
                        spotColor = primaryAccent,
                        ambientColor = primaryAccent
                    )
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                primaryAccent,
                                secondaryAccent,
                                DarkBg
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(200f, 200f)
                        )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = TextPrimary),
                        onClick = {
                            if (isSpeaking) {
                                onStopSpeakingClick()
                            } else {
                                onMicClick()
                            }
                        }
                    )
                    .testTag("voice_orb_button")
            ) {
                Icon(
                    imageVector = when {
                        isSpeaking -> Icons.Default.VolumeUp
                        isListening -> Icons.Default.Mic
                        else -> Icons.Default.Mic
                    },
                    contentDescription = if (isListening) "Sun raha hoon" else "Mic dabayein",
                    tint = if (isSpeaking) DarkBg else TextPrimary,
                    modifier = Modifier.size(46.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Visualizer waveform bars when listening or speaking
        if (isListening || isSpeaking) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(24.dp)
            ) {
                for (i in 0 until 9) {
                    val barHeight = if (isListening) {
                        (8f + (audioLevel * 16f) + (sin(wavePhase + i * 0.8f) * 6f)).coerceIn(4f, 22f).dp
                    } else {
                        (8f + (sin(wavePhase * 2 + i * 0.6f) * 8f)).coerceIn(4f, 20f).dp
                    }
                    Box(
                        modifier = Modifier
                            .size(width = 4.dp, height = barHeight)
                            .clip(CircleShape)
                            .background(primaryAccent)
                    )
                    Spacer(modifier = Modifier.size(3.dp))
                }
            }
        } else {
            Spacer(modifier = Modifier.height(24.dp))
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Status badge
        Text(
            text = statusText,
            color = if (isListening) ElectricCyan else if (isSpeaking) StudioGold else TextSecondary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.testTag("orb_status_text")
        )
    }
}
