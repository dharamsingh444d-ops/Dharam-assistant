package com.example.assistant

enum class MessageSender {
    USER,
    ASSISTANT
}

enum class ActionType {
    NONE,
    TIME,
    YOUTUBE,
    GOOGLE,
    BEAT_STUDIO,
    RAP_BARS,
    WEATHER,
    STOP
}

data class AssistantMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: ActionType = ActionType.NONE,
    val actionPayload: String? = null
)
