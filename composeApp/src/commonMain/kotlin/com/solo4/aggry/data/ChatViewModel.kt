package com.solo4.aggry.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solo4.aggry.db.AggryDatabaseProvider
import com.solo4.aggry.db.ChatRepository
import com.solo4.aggry.provider.AIChatProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ChatViewModel(
    private val provider: AIChatProvider,
    private val apiKeyId: String,
    private val conversationId: String? = null,
    private val repository: ChatRepository = ChatRepository()
) : ViewModel() {

    private var currentConversationId: String? = conversationId
    private var savedModelId: String? = null

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        if (conversationId != null) {
            loadConversationData(conversationId)
        }
        loadModels()
    }

    private fun loadConversationData(convId: String) {
        viewModelScope.launch {
            val messages = repository.getMessages(convId)
            _uiState.update { it.copy(messages = messages) }
            val conversation = repository.getConversation(convId)
            savedModelId = conversation?.modelId
            trySelectSavedModel()
        }
    }

    private fun trySelectSavedModel() {
        val modelId = savedModelId ?: return
        val models = _uiState.value.availableModels
        if (models.isEmpty()) return
        val model = models.find { it.id == modelId }
        if (model != null && _uiState.value.selectedModel == null) {
            _uiState.update { it.copy(selectedModel = model) }
        }
    }

    fun loadModels() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingModels = true) }
            provider.getModels()
                .onSuccess { models ->
                    _uiState.update { it.copy(availableModels = models, isLoadingModels = false) }
                    trySelectSavedModel()
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
        savedModelId = model.id
        currentConversationId?.let { convId ->
            viewModelScope.launch {
                repository.updateConversationModel(convId, model.id, model.name)
            }
        }
    }

    fun attachFiles(files: List<AttachedFile>) {
        _uiState.update { it.copy(attachedFiles = it.attachedFiles + files) }
    }

    fun removeFile(index: Int) {
        _uiState.update {
            val updated = it.attachedFiles.toMutableList().apply { removeAt(index) }
            it.copy(attachedFiles = updated)
        }
    }

    fun clearAttachedFiles() {
        _uiState.update { it.copy(attachedFiles = emptyList()) }
    }

    fun canSend(): Boolean {
        val state = _uiState.value
        return (state.messageInput.isNotBlank() || state.attachedFiles.isNotEmpty())
                && !state.isSending
                && state.selectedModel != null
    }

    @OptIn(ExperimentalUuidApi::class)
    fun sendMessage() {
        val text = _uiState.value.messageInput.trim()
        val files = _uiState.value.attachedFiles.toList()
        if (text.isBlank() && files.isEmpty()) return

        val selectedModel = _uiState.value.selectedModel ?: return

        val userMessage = ChatMessage(
            id = Uuid.random().toString(),
            content = text,
            isFromUser = true,
            attachedFiles = files
        )

        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                messageInput = "",
                attachedFiles = emptyList(),
                isSending = true
            )
        }

        viewModelScope.launch {
            if (currentConversationId == null) {
                currentConversationId = repository.createConversation(
                    apiKeyId = apiKeyId,
                    modelId = selectedModel.id,
                    modelName = selectedModel.name
                )
            }
            currentConversationId?.let { convId ->
                repository.saveMessage(convId, userMessage)
            }

            provider.sendMessage(_uiState.value.messages, selectedModel.id)
                .onSuccess { chatResponse ->
                    val generatedImages = chatResponse.images.mapNotNull { bytes ->
                        try {
                            val path = AggryDatabaseProvider.fileCache.saveBytes(bytes, "generated_image.png")
                            GeneratedImage(cachedPath = path, mimeType = "image/png")
                        } catch (_: Exception) {
                            null
                        }
                    }
                    val aiMessage = ChatMessage(
                        id = Uuid.random().toString(),
                        content = chatResponse.text,
                        isFromUser = false,
                        generatedImages = generatedImages
                    )
                    _uiState.update {
                        it.copy(messages = it.messages + aiMessage, isSending = false)
                    }
                    currentConversationId?.let { convId ->
                        repository.saveMessage(convId, aiMessage)
                    }
                }
                .onFailure { error ->
                    com.solo4.aggry.log.log(
                        com.solo4.aggry.log.LogLevel.ERROR,
                        "ChatViewModel",
                        "sendMessage failed: ${error::class.simpleName}: ${error.message}"
                    )
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
    val error: String? = null,
    val attachedFiles: List<AttachedFile> = emptyList()
)

fun formatFileSize(bytes: Int): String {
    return when {
        bytes >= 1_048_576 -> "${bytes / 1_048_576}MB"
        bytes >= 1024 -> "${bytes / 1024}KB"
        else -> "${bytes}B"
    }
}
