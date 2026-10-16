package com.example.studio

data class RhymeGroup(
    val sound: String,
    val words: List<String>
)

object RapStudioState {
    val sampleRhymes = listOf(
        RhymeGroup("AALA / LALA", listOf("Lala", "Bala", "Jwala", "Chala", "Ujala", "Nirala", "Mala")),
        RhymeGroup("AAR / BAAR", listOf("Raftaar", "Taiyar", "Kalaakaar", "Sansar", "Shandaar", "Hathiyar")),
        RhymeGroup("EAT / BEAT", listOf("Beat", "Street", "Heat", "Complete", "Repeat", "Fleet")),
        RhymeGroup("OW / FLOW", listOf("Flow", "Glow", "Slow", "Show", "Grow", "Blow")),
        RhymeGroup("OSH / JOSH", listOf("Josh", "Hosh", "Kharosh", "Aakrosh", "Madhosh")),
        RhymeGroup("AAG / BHAAG", listOf("Aag", "Bhaag", "Daag", "Raag", "Jaag", "Suhag"))
    )

    val rapStarters = listOf(
        "Subhah utha dekha aasmaan, beat pe chala mera toofan...",
        "Studio ki light on, mic pe hai shor, rukega nahi lala ab koi daur...",
        "Shehar ki sadkon se leke stage tak, har jagah shor hai apne name tak...",
        "Bole toh seedha drill pe flow, sabse aage apun let's go!"
    )
}
