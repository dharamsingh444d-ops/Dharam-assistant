package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioGold
import com.example.ui.theme.TextPrimary

data class QuickPrompt(
    val label: String,
    val query: String
)

@Composable
fun QuickActionChips(
    onSelectPrompt: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val prompts = listOf(
        QuickPrompt("⏰ Samay kya hai?", "time kya hai"),
        QuickPrompt("🎧 Rap Beat Bajao", "beat bajao"),
        QuickPrompt("▶️ YouTube Kholo", "youtube kholo"),
        QuickPrompt("🔍 Google Search", "google"),
        QuickPrompt("🔥 Freestyle Bars", "freestyle rap bars sunao"),
        QuickPrompt("⚡ Hype Quote", "josh bhari shayari sunao"),
        QuickPrompt("⛅ Mausam", "aaj mausam kaisa hai"),
        QuickPrompt("👋 Alvida / Bye", "alvida dharam")
    )

    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        prompts.forEach { prompt ->
            FilterChip(
                selected = false,
                onClick = { onSelectPrompt(prompt.query) },
                label = {
                    Text(
                        text = prompt.label,
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = DarkSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = false,
                    borderColor = DarkCardBorder
                ),
                modifier = Modifier.testTag("quick_chip_${prompt.query.take(6)}")
            )
        }
    }
}
