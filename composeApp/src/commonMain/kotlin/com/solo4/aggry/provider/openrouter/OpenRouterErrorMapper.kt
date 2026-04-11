package com.solo4.aggry.provider.openrouter

import aggry.composeapp.generated.resources.Res
import aggry.composeapp.generated.resources.error_network
import aggry.composeapp.generated.resources.error_timeout
import aggry.composeapp.generated.resources.error_unknown
import com.solo4.aggry.provider.model.Localizable
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlinx.io.IOException
import org.jetbrains.compose.resources.getString

class OpenRouterErrorMapper {

    suspend fun map(exception: Throwable): Exception {
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
                    getString(Res.string.error_timeout),
                    exception
                )
            }

            is IOException -> {
                OtherException(
                    getString(Res.string.error_network),
                    exception
                )
            }

            else -> {
                OtherException(
                    getString(Res.string.error_unknown),
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