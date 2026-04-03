package com.solo4.aggry

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.activity.compose.BackHandler
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
            val navStack = remember { mutableStateListOf<Screen>(Screen.KeyList) }
            val currentScreen = navStack.last()

            fun push(screen: Screen) {
                navStack.add(screen)
            }

            fun pop() {
                if (navStack.size > 1) navStack.removeLast()
            }

            BackHandler(enabled = navStack.size > 1) {
                pop()
            }

            when (val screen = currentScreen) {
                is Screen.KeyList -> {
                    ApiKeyScreen(
                        onKeyClick = { apiKey ->
                            push(Screen.ConversationList(apiKey))
                        }
                    )
                }
                is Screen.ConversationList -> {
                    ConversationListScreen(
                        apiKey = screen.apiKey,
                        onConversationClick = { conversation ->
                            push(
                                Screen.Chat(
                                    apiKey = screen.apiKey,
                                    conversationId = conversation.id
                                )
                            )
                        },
                        onNewChat = {
                            push(Screen.Chat(apiKey = screen.apiKey))
                        },
                        onBack = { pop() }
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
                        onBack = { pop() }
                    )
                }
            }
        }
    }
}
