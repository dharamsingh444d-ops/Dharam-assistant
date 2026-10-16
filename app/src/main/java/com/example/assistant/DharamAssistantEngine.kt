package com.example.assistant

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class EngineResult(
    val speechText: String,
    val displayText: String = speechText,
    val actionType: ActionType = ActionType.NONE,
    val actionPayload: String? = null
)

class DharamAssistantEngine {

    /**
     * Recreates wish_user() from Python script
     */
    fun getGreeting(): Pair<String, String> {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val initialGreeting = when {
            hour in 0..11 -> "Subhah ho gayi hai! Namaste lala, main hoon Dharam Assistant."
            hour in 12..17 -> "Good afternoon! Namaste, Dharam Assistant aapki seva mein haazir hai."
            else -> "Good evening! Namaste, main aapka Dharam Assistant hoon."
        }
        val followUp = "Bataiye, aaj kya kaam karna hai ya kya naya track banana hai?"
        return Pair(initialGreeting, followUp)
    }

    /**
     * Process user query and return response text and action, matching Python logic
     */
    fun processQuery(rawQuery: String): EngineResult {
        val query = rawQuery.trim().lowercase(Locale.ROOT)

        // 1. Time / Samay / Wakt
        if (query.contains("time") || query.contains("samay") || query.contains("wakt") || query.contains("ghadi") || query.contains("baje")) {
            val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
            val strTime = sdf.format(Date())
            val speech = "Abhi time ho raha hai $strTime"
            return EngineResult(
                speechText = speech,
                displayText = "⏰ $speech",
                actionType = ActionType.TIME,
                actionPayload = strTime
            )
        }

        // 2. YouTube
        if (query.contains("youtube") || query.contains("yt") || query.contains("video")) {
            val searchQuery = extractSearchTerm(query, listOf("youtube", "yt", "open", "kholo", "chalao", "play", "pe", "par"))
            val targetUrl = if (searchQuery.isNotBlank()) {
                "https://www.youtube.com/results?search_query=" + java.net.URLEncoder.encode(searchQuery, "UTF-8")
            } else {
                "https://www.youtube.com"
            }
            val speech = if (searchQuery.isNotBlank()) {
                "YouTube par '$searchQuery' dhoondh raha hoon lala..."
            } else {
                "YouTube khol raha hoon..."
            }
            return EngineResult(
                speechText = speech,
                displayText = "▶️ $speech",
                actionType = ActionType.YOUTUBE,
                actionPayload = targetUrl
            )
        }

        // 3. Google
        if (query.contains("google") || query.contains("search") || query.contains("dhoondo") || query.contains("khojo")) {
            val searchQuery = extractSearchTerm(query, listOf("google", "search", "karo", "dhoondo", "khojo", "open", "kholo", "pe", "par"))
            val targetUrl = if (searchQuery.isNotBlank()) {
                "https://www.google.com/search?q=" + java.net.URLEncoder.encode(searchQuery, "UTF-8")
            } else {
                "https://www.google.com"
            }
            val speech = if (searchQuery.isNotBlank()) {
                "Google par '$searchQuery' search kar raha hoon..."
            } else {
                "Google khol raha hoon..."
            }
            return EngineResult(
                speechText = speech,
                displayText = "🔍 $speech",
                actionType = ActionType.GOOGLE,
                actionPayload = targetUrl
            )
        }

        // 4. Beat / Rap / Music
        if (query.contains("beat") || query.contains("rap") || query.contains("music") || query.contains("dhun") || query.contains("gaana") || query.contains("track")) {
            val speech = "Sahi hai lala! Naye rap beats aur lyrics ke liye full energy ke sath kaam shuru karte hain."
            return EngineResult(
                speechText = speech,
                displayText = "🎧 $speech\n\nStudio mein Boom Bap, Trap 808 aur Desi Drill beats taiyar hain!",
                actionType = ActionType.BEAT_STUDIO,
                actionPayload = "OPEN_STUDIO"
            )
        }

        // 5. Stop / Alvida / Bye
        if (query.contains("stop") || query.contains("alvida") || query.contains("bye") || query.contains("ruko") || query.contains("chup") || query.contains("tata")) {
            val speech = "Thik hai, phir milte hain! Apna khayal rakhna."
            return EngineResult(
                speechText = speech,
                displayText = "👋 $speech",
                actionType = ActionType.STOP
            )
        }

        // 6. Rap Freestyle / Bars
        if (query.contains("bars") || query.contains("freestyle") || query.contains("lyrics") || query.contains("rhyme")) {
            val rapBars = getRandomRapBars()
            return EngineResult(
                speechText = "Suno lala yeh bars: $rapBars",
                displayText = "🔥 Dharam Rap Drop:\n\n$rapBars",
                actionType = ActionType.RAP_BARS,
                actionPayload = rapBars
            )
        }

        // 7. Hype quote / Shayari / Dialogue
        if (query.contains("quote") || query.contains("dialogue") || query.contains("shayari") || query.contains("josh") || query.contains("motivation")) {
            val quote = getRandomHypeQuote()
            return EngineResult(
                speechText = quote,
                displayText = "⚡ $quote",
                actionType = ActionType.NONE
            )
        }

        // 8. Mausam / Weather
        if (query.contains("mausam") || query.contains("weather") || query.contains("barish") || query.contains("garmi") || query.contains("sardi")) {
            val speech = "Mausam ekdum mast hai lala! Chahe dhoop ho ya barish, apna beat hamesha garam rehta hai."
            return EngineResult(
                speechText = speech,
                displayText = "⛅ $speech",
                actionType = ActionType.WEATHER
            )
        }

        // 9. Identity / Name
        if (query.contains("kaun ho") || query.contains("naam") || query.contains("who are you") || query.contains("dharam")) {
            val speech = "Main hoon Dharam Assistant! Lala, voice command ho ya desi rap beats, har cheez mein apun sabse aage hai."
            return EngineResult(
                speechText = speech,
                displayText = "🤖 $speech"
            )
        }

        // 10. How are you / Kaise ho
        if (query.contains("kaise ho") || query.contains("kaisa hai") || query.contains("how are you")) {
            val speech = "Ekdum badhiya lala! Full energy aur high bass mode on hai. Aap sunao kya scene hai?"
            return EngineResult(
                speechText = speech,
                displayText = "🔥 $speech"
            )
        }

        // 11. Fallback with helpful tone
        val fallback = "Sun liya lala! Aapne kaha: '$rawQuery'. Kya isko Google pe dhoondhein ya naye rap track pe likhein?"
        return EngineResult(
            speechText = fallback,
            displayText = "🤖 $fallback",
            actionType = ActionType.GOOGLE,
            actionPayload = "https://www.google.com/search?q=" + java.net.URLEncoder.encode(rawQuery, "UTF-8")
        )
    }

    private fun extractSearchTerm(query: String, ignoreWords: List<String>): String {
        var clean = query
        for (w in ignoreWords) {
            clean = clean.replace(Regex("\\b$w\\b", RegexOption.IGNORE_CASE), " ")
        }
        return clean.trim().replace(Regex("\\s+"), " ")
    }

    private fun getRandomHypeQuote(): String {
        val quotes = listOf(
            "Apna time aayega nahi lala, apna time apan khud layega! Full hard!",
            "Mehnat itni shanti se karo ki tumhara rap aur success pure shehar mein goonj jaye.",
            "Zindagi mein rhythm aur bass dono zaroori hain, flow mat tooto!",
            "Asli hip-hop woh hai jo dil se nikle aur mic pe aag laga de.",
            "Har musibat ko drop beat samajh kar jump karo, jeet tumhari hi hogi."
        )
        return quotes.random()
    }

    private fun getRandomRapBars(): String {
        val bars = listOf(
            "Mic pe aate hi macha diya shor,\nBeat pe flow mera unstoppable core!\nKalam mein aag aur beat pe zor,\nLala humse na takrana any more!",
            "Gali gali mein bajta hai bass,\nHard work se jeetenge yeh race!\nNegative vibe ka no place,\nDharam Assistant on the case!",
            "Zero se shuru karke banenge hero,\nBeat pe loop hai 808 sub,\nAsli kalakar kabhi rukte nahi,\nLala mehnat se hilayenge sab!"
        )
        return bars.random()
    }
}
