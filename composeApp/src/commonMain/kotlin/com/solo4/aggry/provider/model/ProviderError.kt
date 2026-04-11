package com.solo4.aggry.provider.model

import com.solo4.aggry.provider.openrouter.OpenRouterErrorType

abstract class ProviderError(
    override val message: String?
) : Exception(message), Localizable {

    abstract val type: OpenRouterErrorType
    abstract val errorDto: ProviderErrorDto?
}