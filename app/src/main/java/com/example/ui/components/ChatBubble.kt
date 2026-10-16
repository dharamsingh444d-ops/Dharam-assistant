package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assistant.ActionType
import com.example.assistant.AssistantMessage
import com.example.assistant.MessageSender
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatBubble(
    message: AssistantMessage,
    onSpeakClick: (String) -> Unit,
    onActionClick: (ActionType, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.sender == MessageSender.USER
    val clipboardManager = LocalClipboardManager.current
    val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 12.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Sender title row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp, start = if (isUser) 0.dp else 4.dp, end = if (isUser) 4.dp else 0.dp)
        ) {
            Text(
                text = if (isUser) "👤 Aapne kaha" else "🤖 Dharam Assistant",
                color = if (isUser) ElectricCyan else StudioGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = timeStr,
                color = TextMuted,
                fontSize = 11.sp
            )
        }

        // Bubble Card
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) {
                        Brush.linearGradient(
                            listOf(
                                DarkSurfaceVariant,
                                DarkSurfaceVariant.copy(alpha = 0.9f)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(
                                DarkSurface,
                                DarkSurfaceVariant
                            )
                        )
                    }
                )
                .border(
                    width = 1.dp,
                    color = if (isUser) ElectricCyan.copy(alpha = 0.35f) else StudioGold.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(14.dp)
                .testTag(if (isUser) "user_chat_bubble" else "assistant_chat_bubble")
        ) {
            Column {
                Text(
                    text = message.text,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                // Optional Action buttons inside bubble
                if (!isUser) {
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Replay TTS voice
                        AssistChip(
                            onClick = { onSpeakClick(message.text) },
                            label = { Text("Sunna hai", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Speak",
                                    tint = StudioGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = DarkSurfaceVariant,
                                labelColor = TextPrimary
                            ),
                            border = AssistChipDefaults.assistChipBorder(true, borderColor = StudioGold.copy(alpha = 0.4f)),
                            modifier = Modifier.testTag("bubble_speak_chip")
                        )

                        // Action-specific trigger chip
                        when (message.actionType) {
                            ActionType.YOUTUBE -> {
                                AssistChip(
                                    onClick = { onActionClick(message.actionType, message.actionPayload) },
                                    label = { Text("Open YouTube", fontSize = 12.sp) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "YouTube",
                                            tint = ElectricCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = DarkSurfaceVariant,
                                        labelColor = ElectricCyan
                                    ),
                                    border = AssistChipDefaults.assistChipBorder(true, borderColor = ElectricCyan.copy(alpha = 0.4f))
                                )
                            }
                            ActionType.GOOGLE -> {
                                AssistChip(
                                    onClick = { onActionClick(message.actionType, message.actionPayload) },
                                    label = { Text("Google Search", fontSize = 12.sp) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Google",
                                            tint = ElectricCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = DarkSurfaceVariant,
                                        labelColor = ElectricCyan
                                    ),
                                    border = AssistChipDefaults.assistChipBorder(true, borderColor = ElectricCyan.copy(alpha = 0.4f))
                                )
                            }
                            ActionType.BEAT_STUDIO, ActionType.RAP_BARS -> {
                                AssistChip(
                                    onClick = { onActionClick(ActionType.BEAT_STUDIO, null) },
                                    label = { Text("Studio Beats", fontSize = 12.sp) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.MusicNote,
                                            contentDescription = "Beats",
                                            tint = StudioAmber,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = DarkSurfaceVariant,
                                        labelColor = StudioAmber
                                    ),
                                    border = AssistChipDefaults.assistChipBorder(true, borderColor = StudioAmber.copy(alpha = 0.4f))
                                )
                            }
                            else -> {}
                        }

                        // Copy chip
                        AssistChip(
                            onClick = { clipboardManager.setText(AnnotatedString(message.text)) },
                            label = { Text("Copy", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = DarkSurfaceVariant,
                                labelColor = TextSecondary
                            ),
                            border = AssistChipDefaults.assistChipBorder(true, borderColor = DarkCardBorder)
                        )
                    }
                }
            }
        }
    }
}
