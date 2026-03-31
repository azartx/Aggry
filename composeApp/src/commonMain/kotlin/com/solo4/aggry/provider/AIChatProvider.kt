package com.solo4.aggry.provider

import com.solo4.aggry.data.AIModel
import com.solo4.aggry.data.ChatMessage

interface AIChatProvider {

    suspend fun getModels(): Result<List<AIModel>>

    suspend fun sendMessage(
        messages: List<ChatMessage>,
        modelId: String
    ): Result<String>
}
