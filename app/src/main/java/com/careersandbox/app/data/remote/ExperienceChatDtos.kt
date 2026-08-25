package com.careersandbox.app.data.remote

// One turn of prior conversation, sent back so the backend has context
data class ChatHistoryTurn(
    val speaker: String, // "user" or "assistant"
    val text: String,
)

// Request body for POST /experience-chat/turn
data class ChatTurnRequest(
    val history: List<ChatHistoryTurn>,
    val answer: String,
)

// Response body from the backend
data class ChatTurnResponse(
    val nextQuestion: String?,
    val extractedFields: ChatExtractedFields,
    val done: Boolean,
)

data class ChatExtractedFields(
    val role: String = "",
    val action: String = "",
    val result: String = "",
    val learning: String = "",
    val title: String = "",
)