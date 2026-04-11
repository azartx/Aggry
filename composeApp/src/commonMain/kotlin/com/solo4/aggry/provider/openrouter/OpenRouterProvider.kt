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
import com.solo4.aggry.log.LogLevel
import com.solo4.aggry.log.log
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlin.time.Clock

private const val ALL_MODELS_ENDPOINT = "https://openrouter.ai/api/v1/models?output_modalities=all"
private const val TAG = "OpenRouterProvider"

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

        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 60_000
        }
    }

    private val errorMapper = OpenRouterErrorMapper()

    override suspend fun getModels(): Result<List<AIModel>> {
        return runCatching {
            val response = client.get(ALL_MODELS_ENDPOINT) {
                header(HttpHeaders.Authorization, "Bearer $apiKey")
            }

            checkOkResponse(response)

            val responseBody = response.body<OpenRouterModelsResponse>()

            responseBody.data.map {
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
            .onFailure { throwable ->
                return Result.failure(errorMapper.map(throwable))
            }
    }

    override suspend fun sendMessage(
        messages: List<ChatMessage>,
        modelId: String
    ): Result<ChatResponse> {
        return runCatching {
            log(
                LogLevel.INFO,
                TAG,
                "sendMessage: modelId=$modelId messages=${messages.size} lastIsFromUser=${messages.lastOrNull()?.isFromUser}"
            )
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
                modalities = buildModalities(modelId = modelId, wantsImageOutput = wantsImageOutput)
            )

            log(
                LogLevel.DEBUG,
                TAG,
                "Request modalities=${request.modalities} openRouterMessages=${openRouterMessages.size} wantsImageOutput=$wantsImageOutput"
            )

            val httpResponse = client.post("https://openrouter.ai/api/v1/chat/completions") {
                header(HttpHeaders.Authorization, "Bearer $apiKey")
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            checkOkResponse(httpResponse)

            val responseBotyText = httpResponse.bodyAsText()
            log(LogLevel.DEBUG, TAG, "Response body: ${responseBotyText.trim()}")
            val response: OpenRouterChatResponse = httpResponse.body()
            log(LogLevel.DEBUG, TAG, "Response received: choices=${response.choices.size}")

            if (response.choices.isEmpty()) {
                // Handle case where no choices are returned
                throw Exception("Не получен ответ от модели. Попробуйте снова.")
            }

            val choiceMessage = response.choices.first().message
            val text = choiceMessage.content ?: ""
            log(
                LogLevel.DEBUG,
                TAG,
                "Assistant content len=${text.length} images present=${choiceMessage.images?.size ?: 0}"
            )
            val images = choiceMessage.images?.mapNotNull { img ->
                val url = img.image_url?.url ?: return@mapNotNull null
                log(LogLevel.DEBUG, TAG, "Decoding generated image dataUrl len=${url.length}")
                decodeBase64DataUrl(url)
            } ?: emptyList()

            log(
                LogLevel.INFO,
                TAG,
                "Success: textLen=${text.length} images=${images.size}"
            )
            ChatResponse(text = text, images = images)
        }
            .onFailure { throwable ->
                // Handle various exception types
                val error = errorMapper.map(throwable)

                log(LogLevel.ERROR, "OpenRouter", error.stackTraceToString())

                return Result.failure(error)
            }
    }

    /**
     * Thrown an exception if response code is not in 200..299 range
     * */
    private suspend fun checkOkResponse(response: HttpResponse) {
        if (response.status.value !in 200..299) {
            val errorResponseBody = response.bodyAsText()
            log(LogLevel.DEBUG, TAG, "Error response body: $errorResponseBody")

            throw OpenRouterErrorHandler.parseErrorResponse(errorResponseBody)
                ?: Exception(errorResponseBody)
        }
    }

    private var modelsCache: List<AIModel>? = null
    private var modelsCacheAtMs: Long = 0
    private val modelsCacheTtlMs: Long = 10 * 60 * 1000

    private suspend fun getModelsCachedOnceOrNull(): List<AIModel>? {
        val now = Clock.System.now().toEpochMilliseconds()
        val cached = modelsCache
        if (cached != null && now - modelsCacheAtMs < modelsCacheTtlMs) return cached
        val res = getModels()
        val models = res.getOrNull() ?: return null
        modelsCache = models
        modelsCacheAtMs = now
        return models
    }

    private suspend fun buildModalities(
        modelId: String,
        wantsImageOutput: Boolean
    ): List<String>? {
        if (!wantsImageOutput) return null

        val models = getModelsCachedOnceOrNull() ?: return listOf("image")
        val model = models.firstOrNull { it.id == modelId } ?: return listOf("image")

        val result = buildList {
            if ("image" in model.outputModalities) add("image")
            // Only include "text" if the model supports it for output.
            if ("text" in model.outputModalities) add("text")
        }
        return result.ifEmpty { listOf("image") }
    }

    private fun decodeBase64DataUrl(dataUrl: String): ByteArray? {
        val prefix = "base64,"
        val idx = dataUrl.indexOf(prefix)
        if (idx < 0) return null
        val base64 = dataUrl.substring(idx + prefix.length)
        return try {
            val bytes = base64.decodeBase64()
            bytes
        } catch (t: Throwable) {
            log(
                LogLevel.ERROR,
                "OpenRouterProvider",
                "Failed to decode base64 image: ${t.message}",
                t
            )
            null
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
                sextets.add(
                    when {
                        c in 'A'..'Z' -> c - 'A'
                        c in 'a'..'z' -> c - 'a' + 26
                        c in '0'..'9' -> c - '0' + 52
                        c == '+' -> 62
                        c == '/' -> 63
                        else -> 0
                    }
                )
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
