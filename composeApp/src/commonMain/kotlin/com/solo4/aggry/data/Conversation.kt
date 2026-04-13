package com.solo4.aggry.data

data class Conversation(
    val id: String,
    val apiKeyId: String,
    val modelId: String,
    val modelName: String,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long,
    val messageCount: Int = 0,
    val lastMessage: String? = null
)
