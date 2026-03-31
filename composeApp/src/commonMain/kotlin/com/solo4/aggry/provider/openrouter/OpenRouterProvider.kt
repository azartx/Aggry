package com.solo4.aggry.provider.openrouter

import com.solo4.aggry.data.AIModel
import com.solo4.aggry.data.ChatMessage
import com.solo4.aggry.provider.AIChatProvider
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class OpenRouterProvider(
    private val apiKey: String
) : AIChatProvider {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    override suspend fun getModels(): Result<List<AIModel>> {
        return runCatching {
            val response: OpenRouterModelsResponse = client.get("https://openrouter.ai/api/v1/models") {
                header(HttpHeaders.Authorization, "Bearer $apiKey")
            }.body()
            response.data.map {
                AIModel(
                    id = it.id,
                    name = it.name,
                    inputModalities = it.architecture?.input_modalities ?: emptyList(),
                    outputModalities = it.architecture?.output_modalities ?: emptyList(),
                    contextLength = it.context_length
                )
            }
        }
    }

    override suspend fun sendMessage(
        messages: List<ChatMessage>,
        modelId: String
    ): Result<String> {
        return runCatching {
            val request = OpenRouterChatRequest(
                model = modelId,
                messages = messages.map { msg ->
                    OpenRouterMessage(
                        role = if (msg.isFromUser) "user" else "assistant",
                        content = msg.content
                    )
                }
            )
            val response: OpenRouterChatResponse = client.post("https://openrouter.ai/api/v1/chat/completions") {
                header(HttpHeaders.Authorization, "Bearer $apiKey")
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
            response.choices.first().message.content
        }
    }
}
