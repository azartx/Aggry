package com.solo4.aggry.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ApiKeyViewModel : ViewModel() {

    private val storage = ApiKeyStorage()

    private val _uiState = MutableStateFlow(ApiKeyUiState())
    val uiState: StateFlow<ApiKeyUiState> = _uiState.asStateFlow()

    init {
        loadKeys()
    }

    private fun loadKeys() {
        val keys = storage.getKeys()
        _uiState.update { it.copy(apiKeys = keys) }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun addKey(name: String, key: String) {
        if (name.isBlank() || key.isBlank()) return
        val apiKey = ApiKey(
            id = Uuid.random().toString(),
            name = name.trim(),
            key = key.trim()
        )
        storage.addKey(apiKey)
        loadKeys()
    }

    fun removeKey(id: String) {
        storage.removeKey(id)
        loadKeys()
    }

    fun updateNameInput(value: String) {
        _uiState.update { it.copy(nameInput = value) }
    }

    fun updateKeyInput(value: String) {
        _uiState.update { it.copy(keyInput = value) }
    }

    fun clearInputs() {
        _uiState.update { it.copy(nameInput = "", keyInput = "") }
    }
}

data class ApiKeyUiState(
    val apiKeys: List<ApiKey> = emptyList(),
    val nameInput: String = "",
    val keyInput: String = ""
)
