package com.solo4.aggry

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.solo4.aggry.data.ApiKey
import com.solo4.aggry.data.ChatViewModel
import com.solo4.aggry.provider.openrouter.OpenRouterProvider

private sealed interface Screen {
    data object KeyList : Screen
    data class ConversationList(val apiKey: ApiKey) : Screen
    data class Chat(val apiKey: ApiKey, val conversationId: String? = null) : Screen
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
        ) {
            var currentScreen by remember { mutableStateOf<Screen>(Screen.KeyList) }

            when (val screen = currentScreen) {
                is Screen.KeyList -> {
                    ApiKeyScreen(
                        onKeyClick = { apiKey ->
                            currentScreen = Screen.ConversationList(apiKey)
                        }
                    )
                }
                is Screen.ConversationList -> {
                    ConversationListScreen(
                        apiKey = screen.apiKey,
                        onConversationClick = { conversation ->
                            currentScreen = Screen.Chat(
                                apiKey = screen.apiKey,
                                conversationId = conversation.id
                            )
                        },
                        onNewChat = {
                            currentScreen = Screen.Chat(apiKey = screen.apiKey)
                        },
                        onBack = { currentScreen = Screen.KeyList }
                    )
                }
                is Screen.Chat -> {
                    val viewModel = remember(screen.apiKey.id, screen.conversationId) {
                        ChatViewModel(
                            provider = OpenRouterProvider(screen.apiKey.key),
                            apiKeyId = screen.apiKey.id,
                            conversationId = screen.conversationId
                        )
                    }
                    ChatScreen(
                        viewModel = viewModel,
                        onBack = {
                            currentScreen = Screen.ConversationList(screen.apiKey)
                        }
                    )
                }
            }
        }
    }
}
