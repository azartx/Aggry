package com.solo4.aggry.data

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.serialization.builtins.ListSerializer

@OptIn(ExperimentalSettingsApi::class)
class ApiKeyStorage(private val settings: Settings = createSettings()) {

    private val keysKey = "api_keys"
    private val serializer = ListSerializer(ApiKey.serializer())

    fun saveKeys(keys: List<ApiKey>) {
        settings.encodeValue(serializer, keysKey, keys)
    }

    fun getKeys(): List<ApiKey> {
        return settings.decodeValueOrNull(serializer, keysKey) ?: emptyList()
    }

    fun addKey(apiKey: ApiKey) {
        val keys = getKeys().toMutableList()
        keys.add(apiKey)
        saveKeys(keys)
    }

    fun removeKey(id: String) {
        val keys = getKeys().toMutableList()
        keys.removeAll { it.id == id }
        saveKeys(keys)
    }
}
