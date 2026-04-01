package com.solo4.aggry.provider

import com.solo4.aggry.data.AIModel
import com.solo4.aggry.data.ChatMessage

data class ChatResponse(
    val text: String,
    val images: List<ByteArray> = emptyList()
)

interface AIChatProvider {

    suspend fun getModels(): Result<List<AIModel>>

    suspend fun sendMessage(
        messages: List<ChatMessage>,
        modelId: String
    ): Result<ChatResponse>
}
