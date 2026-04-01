package com.solo4.aggry.provider.openrouter

import com.solo4.aggry.data.AIModel
import com.solo4.aggry.data.AttachedFile
import com.solo4.aggry.data.ChatMessage
import com.solo4.aggry.provider.AIChatProvider
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*

class OpenRouterProvider(
    private val apiKey: String
) : AIChatProvider {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
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
            val openRouterMessages = messages.map { msg ->
                OpenRouterMessage(
                    role = if (msg.isFromUser) "user" else "assistant",
                    content = buildContent(msg.content, msg.attachedFiles)
                )
            }
            val request = OpenRouterChatRequest(
                model = modelId,
                messages = openRouterMessages
            )
            val response: OpenRouterChatResponse = client.post("https://openrouter.ai/api/v1/chat/completions") {
                header(HttpHeaders.Authorization, "Bearer $apiKey")
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
            response.choices.first().message.content ?: ""
        }
    }

    private fun buildContent(text: String, files: List<AttachedFile>): JsonElement {
        if (files.isEmpty()) {
            return JsonPrimitive(text)
        }

        val parts = buildJsonArray {
            if (text.isNotBlank()) {
                addJsonObject {
                    put("type", "text")
                    put("text", text)
                }
            }
            files.forEach { file ->
                val base64 = file.bytes.encodeBase64()
                val dataUrl = "data:${file.mimeType};base64,$base64"

                when {
                    file.isImage -> {
                        addJsonObject {
                            put("type", "image_url")
                            putJsonObject("image_url") {
                                put("url", dataUrl)
                            }
                        }
                    }
                    file.isPdf -> {
                        addJsonObject {
                            put("type", "file")
                            putJsonObject("file") {
                                put("filename", file.name)
                                put("file_data", dataUrl)
                            }
                        }
                    }
                    else -> {
                        addJsonObject {
                            put("type", "file")
                            putJsonObject("file") {
                                put("filename", file.name)
                                put("file_data", dataUrl)
                            }
                        }
                    }
                }
            }
        }
        return parts
    }
}

private val BASE64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"

fun ByteArray.encodeBase64(): String {
    val input = this
    val output = StringBuilder(((input.size + 2) / 3) * 4)
    var i = 0
    while (i < input.size) {
        val b0 = input[i].toInt() and 0xFF
        val b1 = if (i + 1 < input.size) input[i + 1].toInt() and 0xFF else 0
        val b2 = if (i + 2 < input.size) input[i + 2].toInt() and 0xFF else 0

        val bitmap = (b0 shl 16) or (b1 shl 8) or b2

        output.append(BASE64_ALPHABET[(bitmap shr 18) and 0x3F])
        output.append(BASE64_ALPHABET[(bitmap shr 12) and 0x3F])
        output.append(if (i + 1 < input.size) BASE64_ALPHABET[(bitmap shr 6) and 0x3F] else '=')
        output.append(if (i + 2 < input.size) BASE64_ALPHABET[bitmap and 0x3F] else '=')

        i += 3
    }
    return output.toString()
}
