package com.solo4.aggry.provider.openrouter

import com.solo4.aggry.provider.model.Localizable
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlinx.io.IOException

class OpenRouterErrorMapper {

    fun map(exception: Throwable): Exception {
        return when (exception) {
            is OpenRouterErrorResponse -> {
                val errorType = OpenRouterErrorHandler.getErrorType(exception.error.code)

                OpenRouterProviderException(
                    errorType,
                    exception.error,
                    exception.error.message
                )
            }

            is SocketTimeoutException -> {
                OtherException(
                    "Превышено время ожидания соединения. Проверьте интернет-подключение.",
                    exception
                )
            }

            is IOException -> {
                OtherException(
                    "Не удается подключиться к серверу. Проверьте интернет-подключение.",
                    exception
                )
            }

            else -> {
                OtherException(
                    "Неизвестная ошибка",
                    exception
                )
            }
        }
    }
}

private class OtherException(
    private val localizedMessage: String,
    override val cause: Throwable?,
) : Exception(), Localizable {

    override fun getLocalizedText(): String {
        return localizedMessage
    }
}