package com.solo4.aggry.data

import kotlinx.serialization.Serializable

@Serializable
data class ApiKey(
    val id: String,
    val name: String,
    val key: String
)
