package com.solo4.aggry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.solo4.aggry.data.AIModel
import com.solo4.aggry.data.ChatMessage
import com.solo4.aggry.data.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    uiState.error?.let { error ->
        LaunchedEffect(error) {
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(uiState.selectedModel?.name ?: "Select Model")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<-")
                    }
                },
                actions = {
                    Box {
                        TextButton(
                            onClick = { viewModel.toggleModelPicker() },
                            enabled = !uiState.isLoadingModels
                        ) {
                            if (uiState.isLoadingModels) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = uiState.selectedModel?.name ?: "Model",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = uiState.showModelPicker,
                            onDismissRequest = { viewModel.toggleModelPicker() }
                        ) {
                            uiState.availableModels.forEach { model ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(model.name)
                                            ModelCapabilities(model)
                                        }
                                    },
                                    onClick = { viewModel.selectModel(model) }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            uiState.selectedModel?.let { model ->
                SelectedModelInfo(model = model)
            }
            if (uiState.messages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Send a message to start chatting",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(uiState.messages, key = { it.id }) { message ->
                        MessageBubble(message = message)
                    }

                    if (uiState.isSending) {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(24.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.messageInput,
                    onValueChange = { viewModel.updateMessageInput(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") },
                    singleLine = false,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { viewModel.sendMessage() },
                    enabled = uiState.messageInput.isNotBlank() && !uiState.isSending && uiState.selectedModel != null
                ) {
                    Text("Send")
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isFromUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (message.isFromUser) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.content,
                color = if (message.isFromUser) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ModelCapabilities(model: AIModel) {
    val tags = buildList {
        for (modality in model.inputModalities) {
            add(CapabilityTag("in: $modality", isInput = true))
        }
        for (modality in model.outputModalities) {
            add(CapabilityTag("out: $modality", isInput = false))
        }
    }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        tags.forEach { tag ->
            CapabilityChip(text = tag.text, isInput = tag.isInput)
        }
    }
}

private data class CapabilityTag(val text: String, val isInput: Boolean)

@Composable
private fun CapabilityChip(text: String, isInput: Boolean) {
    val bgColor = if (isInput) MaterialTheme.colorScheme.tertiaryContainer
    else MaterialTheme.colorScheme.primaryContainer
    val textColor = if (isInput) MaterialTheme.colorScheme.onTertiaryContainer
    else MaterialTheme.colorScheme.onPrimaryContainer

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = bgColor
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun SelectedModelInfo(model: AIModel) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = model.name,
                    style = MaterialTheme.typography.titleSmall,
                    overflow = TextOverflow.Ellipsis,
                )
                model.contextLength?.let { length ->
                    Text(
                        text = formatContextLength(length),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            ModelCapabilities(model)
        }
    }
}

private fun formatContextLength(tokens: Long): String {
    return when {
        tokens >= 1_000_000 -> "${tokens / 1_000_000}M ctx"
        tokens >= 1_000 -> "${tokens / 1_000}K ctx"
        else -> "$tokens tokens"
    }
}
