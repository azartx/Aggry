package com.solo4.aggry.provider.openrouter

import com.solo4.aggry.data.AIModel
import com.solo4.aggry.data.AttachedFile
import com.solo4.aggry.data.ChatMessage
import com.solo4.aggry.provider.AIChatProvider
import com.solo4.aggry.provider.ChatResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*

private const val ALL_MODELS_ENDPOINT = "https://openrouter.ai/api/v1/models?output_modalities=all"

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
            val response: OpenRouterModelsResponse = client.get(ALL_MODELS_ENDPOINT) {
                header(HttpHeaders.Authorization, "Bearer $apiKey")
            }.body()
            response.data.map {
                AIModel(
                    id = it.id,
                    name = it.name,
                    inputModalities = it.architecture?.input_modalities ?: emptyList(),
                    outputModalities = it.architecture?.output_modalities ?: emptyList(),
                    contextLength = it.context_length,
                    inputPrice = it.pricing?.prompt?.toDoubleOrNull(),
                    outputPrice = it.pricing?.completion?.toDoubleOrNull()
                )
            }
        }
    }

    override suspend fun sendMessage(
        messages: List<ChatMessage>,
        modelId: String
    ): Result<ChatResponse> {
        return runCatching {
            val lastMessage = messages.lastOrNull()
            // TODO: it is not reliable to check always words in text. it could be maby pressed checkbox which marks chat to generate image
            val wantsImageOutput = lastMessage?.let { msg ->
                msg.content.contains("generate", ignoreCase = true) ||
                msg.content.contains("create", ignoreCase = true) ||
                msg.content.contains("draw", ignoreCase = true) ||
                msg.content.contains("image", ignoreCase = true) ||
                msg.content.contains("picture", ignoreCase = true) ||
                msg.content.contains("photo", ignoreCase = true) ||
                msg.content.contains("изобрази", ignoreCase = true) ||
                msg.content.contains("нарисуй", ignoreCase = true) ||
                msg.content.contains("сгенерируй", ignoreCase = true) ||
                msg.content.contains("картинк", ignoreCase = true)
            } ?: false

            val openRouterMessages = messages.map { msg ->
                OpenRouterMessage(
                    role = if (msg.isFromUser) "user" else "assistant",
                    content = buildContent(msg.content, msg.attachedFiles)
                )
            }

            val request = OpenRouterChatRequest(
                model = modelId,
                messages = openRouterMessages,
                modalities = if (wantsImageOutput) listOf("image", "text") else null
            )

            val response: OpenRouterChatResponse = client.post("https://openrouter.ai/api/v1/chat/completions") {
                header(HttpHeaders.Authorization, "Bearer $apiKey")
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            val choiceMessage = response.choices.first().message
            val text = choiceMessage.content ?: ""
            val images = choiceMessage.images?.mapNotNull { img ->
                val url = img.image_url?.url ?: return@mapNotNull null
                decodeBase64DataUrl(url)
            } ?: emptyList()

            ChatResponse(text = text, images = images)
        }
    }

    private fun decodeBase64DataUrl(dataUrl: String): ByteArray? {
        val prefix = "base64,"
        val idx = dataUrl.indexOf(prefix)
        if (idx < 0) return null
        val base64 = dataUrl.substring(idx + prefix.length)
        return base64.decodeBase64()
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

fun String.decodeBase64(): ByteArray {
    val input = this.replace("\n", "").replace("\r", "").replace(" ", "")
    val padding = input.count { it == '=' }
    val dataLength = (input.length * 6) / 8 - padding
    val output = ByteArray(dataLength)
    var outIdx = 0
    var i = 0
    while (i < input.length) {
        val sextets = mutableListOf<Int>()
        repeat(4) {
            if (i + it < input.length) {
                val c = input[i + it]
                sextets.add(when {
                    c in 'A'..'Z' -> c - 'A'
                    c in 'a'..'z' -> c - 'a' + 26
                    c in '0'..'9' -> c - '0' + 52
                    c == '+' -> 62
                    c == '/' -> 63
                    else -> 0
                })
            } else {
                sextets.add(0)
            }
        }
        val bitmap = (sextets[0] shl 18) or (sextets[1] shl 12) or (sextets[2] shl 6) or sextets[3]
        if (outIdx < dataLength) output[outIdx++] = ((bitmap shr 16) and 0xFF).toByte()
        if (outIdx < dataLength) output[outIdx++] = ((bitmap shr 8) and 0xFF).toByte()
        if (outIdx < dataLength) output[outIdx++] = (bitmap and 0xFF).toByte()
        i += 4
    }
    return output
}
