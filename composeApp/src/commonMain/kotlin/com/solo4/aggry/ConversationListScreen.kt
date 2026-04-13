package com.solo4.aggry

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solo4.aggry.components.*
import com.solo4.aggry.data.ApiKey
import com.solo4.aggry.data.Conversation
import com.solo4.aggry.data.ConversationListViewModel
import aggry.composeapp.generated.resources.Res
import aggry.composeapp.generated.resources.no_conversations
import aggry.composeapp.generated.resources.close_x
import com.solo4.aggry.img.Add
import com.solo4.aggry.img.ArrowBack
import com.solo4.aggry.img.VectorIcons
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListScreen(
    apiKey: ApiKey,
    onConversationClick: (Conversation) -> Unit,
    onNewChat: () -> Unit,
    onBack: () -> Unit,
    viewModel: ConversationListViewModel = remember(apiKey.id) {
        ConversationListViewModel(apiKeyId = apiKey.id)
    }
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = apiKey.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Conversations",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = VectorIcons.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewChat,
                icon = {
                    Icon(
                        imageVector = VectorIcons.Add,
                        contentDescription = "New Chat"
                    )
                },
                text = { Text("New Chat") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        if (uiState.conversations.isEmpty()) {
            EmptyConversationsState(
                apiKeyName = apiKey.name,
                onNewChat = onNewChat,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp, vertical = 48.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
            ) {
                item {
                    GradientText(
                        text = "Recent Conversations",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                items(uiState.conversations, key = { it.id }) { conversation ->
                    AnimatedConversationItem(
                        conversation = conversation,
                        onClick = { onConversationClick(conversation) },
                        onEdit = { /* TODO: Implement edit conversation */ },
                        onDelete = { viewModel.deleteConversation(conversation.id) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedConversationItem(
    conversation: Conversation,
    onClick: () -> Unit,
    onEdit: (Conversation) -> Unit,
    onDelete: (Conversation) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        ConversationCard(
            conversation = conversation,
            onClick = onClick,
            onEdit = onEdit,
            onDelete = onDelete,
            modifier = modifier
        )
    }
}