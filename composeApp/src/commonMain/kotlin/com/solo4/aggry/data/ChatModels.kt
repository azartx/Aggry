package com.solo4.aggry.data

data class AIModel(
    val id: String,
    val name: String
)

data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean
)
