package com.solo4.aggry.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solo4.aggry.db.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConversationListViewModel(
    private val apiKeyId: String,
    private val repository: ChatRepository = ChatRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversationListUiState())
    val uiState: StateFlow<ConversationListUiState> = _uiState.asStateFlow()

    init {
        loadConversations()
    }

    private fun loadConversations() {
        viewModelScope.launch {
            repository.getConversationsByApiKey(apiKeyId)
                .catch {
                    _uiState.update {
                        it.copy(isScreenLoading = false)
                    }
                }
                .collect { conversations ->
                    _uiState.update {
                        it.copy(
                            conversations = conversations,
                            isScreenLoading = false,
                        )
                    }
                }
        }
    }

    fun deleteConversation(conversationId: String) {
        viewModelScope.launch {
            repository.deleteConversation(conversationId)
        }
    }
}

data class ConversationListUiState(
    val conversations: List<Conversation> = emptyList(),
    val isScreenLoading: Boolean = true,
)
