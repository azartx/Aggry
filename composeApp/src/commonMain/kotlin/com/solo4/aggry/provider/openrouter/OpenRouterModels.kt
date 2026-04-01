package com.solo4.aggry.provider.openrouter

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class OpenRouterModelsResponse(
    val data: List<OpenRouterModel>
)

@Serializable
data class OpenRouterModel(
    val id: String,
    val name: String,
    val architecture: OpenRouterArchitecture? = null,
    val context_length: Long? = null
)

@Serializable
data class OpenRouterArchitecture(
    val input_modalities: List<String> = emptyList(),
    val output_modalities: List<String> = emptyList()
)

@Serializable
data class OpenRouterChatRequest(
    val model: String,
    val messages: List<OpenRouterMessage>
)

@Serializable
data class OpenRouterMessage(
    val role: String,
    val content: JsonElement
)

@Serializable
data class OpenRouterChatResponse(
    val choices: List<OpenRouterChoice>
)

@Serializable
data class OpenRouterChoice(
    val message: OpenRouterChoiceMessage
)

@Serializable
data class OpenRouterChoiceMessage(
    val role: String,
    val content: String? = null
)
