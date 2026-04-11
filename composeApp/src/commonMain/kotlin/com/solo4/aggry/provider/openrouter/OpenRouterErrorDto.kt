package com.solo4.aggry.provider.openrouter

import com.solo4.aggry.log.LogLevel
import com.solo4.aggry.log.log
import com.solo4.aggry.provider.model.ProviderError
import com.solo4.aggry.provider.model.ProviderErrorDto
import com.solo4.aggry.provider.model.ProviderErrorType
import com.solo4.aggry.provider.openrouter.OpenRouterErrorHandler.getUserFriendlyMessage
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Represents errors returned by the OpenRouter API
 */
@Serializable
data class OpenRouterErrorDto(
    override val code: Int,
    override val message: String,
    val metadata: Map<String, String>? = null
) : ProviderErrorDto

class OpenRouterProviderException(
    override val type: OpenRouterErrorType,
    override val errorDto: OpenRouterErrorDto?,
    override val message: String?,
) : ProviderError(message) {

    override fun getLocalizedText(): String {
        return getUserFriendlyMessage(type)
    }
}

/**
 * Represents the error response wrapper from OpenRouter API
 */
@Serializable
data class OpenRouterErrorResponse(
    val error: OpenRouterErrorDto
) : Exception(error.message)

/**
 * Enum representing different types of errors that can occur when interacting with OpenRouter
 */
enum class OpenRouterErrorType : ProviderErrorType {
    /** Invalid request parameters */
    BAD_REQUEST,
    /** Invalid or missing API key */
    UNAUTHORIZED,
    /** Insufficient credits */
    PAYMENT_REQUIRED,
    /** Input was flagged by moderation */
    FORBIDDEN,
    /** Request timed out */
    REQUEST_TIMEOUT,
    /** Rate limited */
    TOO_MANY_REQUESTS,
    /** Model is down or invalid response */
    BAD_GATEWAY,
    /** Service unavailable */
    SERVICE_UNAVAILABLE,
    /** Network connectivity issues */
    NETWORK_ERROR,
    /** Timeout waiting for response */
    TIMEOUT_ERROR,
    /** Unknown or unhandled error */
    UNKNOWN
}

/**
 * Utility class for handling OpenRouter errors
 */
object OpenRouterErrorHandler {

    private val json by lazy { Json { ignoreUnknownKeys = true } }

    /**
     * Determines the error type based on HTTP status code and error response
     */
    fun getErrorType(statusCode: Int): OpenRouterErrorType {
        return when (statusCode) {
            400 -> OpenRouterErrorType.BAD_REQUEST
            401 -> OpenRouterErrorType.UNAUTHORIZED
            402 -> OpenRouterErrorType.PAYMENT_REQUIRED
            403 -> OpenRouterErrorType.FORBIDDEN
            408 -> OpenRouterErrorType.REQUEST_TIMEOUT
            429 -> OpenRouterErrorType.TOO_MANY_REQUESTS
            502 -> OpenRouterErrorType.BAD_GATEWAY
            503 -> OpenRouterErrorType.SERVICE_UNAVAILABLE
            else -> OpenRouterErrorType.UNKNOWN
        }
    }

    /**
     * Gets a user-friendly message for an error type
     */
    fun getUserFriendlyMessage(errorType: OpenRouterErrorType): String {
        return when (errorType) {
            OpenRouterErrorType.BAD_REQUEST -> "Неверный запрос. Проверьте параметры и попробуйте снова."
            OpenRouterErrorType.UNAUTHORIZED -> "Недействительный API ключ. Проверьте ваш ключ и попробуйте снова."
            OpenRouterErrorType.PAYMENT_REQUIRED -> "Недостаточно средств на аккаунте. Пополните баланс и попробуйте снова."
            OpenRouterErrorType.FORBIDDEN -> "Ваш запрос был заблокирован системой модерации."
            OpenRouterErrorType.REQUEST_TIMEOUT -> "Превышено время ожидания запроса. Попробуйте снова позже."
            OpenRouterErrorType.TOO_MANY_REQUESTS -> "Превышен лимит запросов. Подождите немного перед повторной попыткой."
            OpenRouterErrorType.BAD_GATEWAY -> "Выбранная модель временно недоступна. Попробуйте другую модель или повторите запрос позже."
            OpenRouterErrorType.SERVICE_UNAVAILABLE -> "Сервис временно недоступен. Попробуйте снова позже."
            OpenRouterErrorType.NETWORK_ERROR -> "Ошибка сети. Проверьте подключение к интернету и попробуйте снова."
            OpenRouterErrorType.TIMEOUT_ERROR -> "Превышено время ожидания ответа от сервера. Попробуйте снова позже."
            OpenRouterErrorType.UNKNOWN -> "Неизвестная ошибка"
        }
    }

    /**
     * Parses error response from JSON string
     */
    fun parseErrorResponse(jsonString: String): OpenRouterErrorResponse? {
        return try {
            json.decodeFromString<OpenRouterErrorResponse>(jsonString)
        } catch (_: Exception) {
            null
        }
    }
}