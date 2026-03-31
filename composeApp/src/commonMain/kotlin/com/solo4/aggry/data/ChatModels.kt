package com.solo4.aggry.data

data class AIModel(
    val id: String,
    val name: String,
    val inputModalities: List<String> = emptyList(),
    val outputModalities: List<String> = emptyList(),
    val contextLength: Long? = null
) {
    val canGenerateImages: Boolean get() = "image" in outputModalities
    val canProcessImages: Boolean get() = "image" in inputModalities
    val canProcessFiles: Boolean get() = "file" in inputModalities
}

data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean
)
