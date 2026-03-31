package com.solo4.aggry.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solo4.aggry.provider.AIChatProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ChatViewModel(
    private val provider: AIChatProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadModels()
    }

    fun loadModels() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingModels = true) }
            provider.getModels()
                .onSuccess { models ->
                    _uiState.update { it.copy(availableModels = models, isLoadingModels = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message ?: "Failed to load models", isLoadingModels = false) }
                }
        }
    }

    fun updateMessageInput(value: String) {
        _uiState.update { it.copy(messageInput = value) }
    }

    fun toggleModelPicker() {
        _uiState.update { it.copy(showModelPicker = !it.showModelPicker) }
    }

    fun selectModel(model: AIModel) {
        _uiState.update { it.copy(selectedModel = model, showModelPicker = false) }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun sendMessage() {
        val text = _uiState.value.messageInput.trim()
        if (text.isBlank()) return

        val selectedModel = _uiState.value.selectedModel ?: return

        val userMessage = ChatMessage(
            id = Uuid.random().toString(),
            content = text,
            isFromUser = true
        )
        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                messageInput = "",
                isSending = true
            )
        }

        viewModelScope.launch {
            provider.sendMessage(_uiState.value.messages, selectedModel.id)
                .onSuccess { response ->
                    val aiMessage = ChatMessage(
                        id = Uuid.random().toString(),
                        content = response,
                        isFromUser = false
                    )
                    _uiState.update {
                        it.copy(messages = it.messages + aiMessage, isSending = false)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(error = error.message ?: "Failed to send message", isSending = false)
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val messageInput: String = "",
    val selectedModel: AIModel? = null,
    val availableModels: List<AIModel> = emptyList(),
    val showModelPicker: Boolean = false,
    val isSending: Boolean = false,
    val isLoadingModels: Boolean = false,
    val error: String? = null
)
