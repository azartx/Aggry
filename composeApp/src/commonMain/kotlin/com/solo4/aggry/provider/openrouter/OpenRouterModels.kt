package com.solo4.aggry.provider.openrouter

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenRouterModelsResponse(
    val data: List<OpenRouterModel>
)

@Serializable
data class OpenRouterModel(
    val id: String,
    val name: String
)

@Serializable
data class OpenRouterChatRequest(
    val model: String,
    val messages: List<OpenRouterMessage>
)

@Serializable
data class OpenRouterMessage(
    val role: String,
    val content: String
)

@Serializable
data class OpenRouterChatResponse(
    val choices: List<OpenRouterChoice>
)

@Serializable
data class OpenRouterChoice(
    val message: OpenRouterMessage
)
