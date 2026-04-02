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
    val context_length: Long? = null,
    val pricing: OpenRouterPricing? = null
)

@Serializable
data class OpenRouterArchitecture(
    val input_modalities: List<String> = emptyList(),
    val output_modalities: List<String> = emptyList()
)

@Serializable
data class OpenRouterPricing(
    val prompt: String? = null,
    val completion: String? = null
)

@Serializable
data class OpenRouterChatRequest(
    val model: String,
    val messages: List<OpenRouterMessage>,
    val modalities: List<String>? = null
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
    val content: String? = null,
    val images: List<OpenRouterImage>? = null
)

@Serializable
data class OpenRouterImage(
    val type: String? = null,
    val image_url: OpenRouterImageUrl? = null
)

@Serializable
data class OpenRouterImageUrl(
    val url: String? = null
)
