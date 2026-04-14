package com.solo4.aggry

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.solo4.aggry.components.chat.*
import com.solo4.aggry.components.GradientText
import com.solo4.aggry.data.*
import com.solo4.aggry.copy.copyToClipboard
import com.solo4.aggry.filepicker.rememberFilePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import aggry.composeapp.generated.resources.Res
import aggry.composeapp.generated.resources.*
import com.solo4.aggry.img.ArrowBack
import com.solo4.aggry.img.VectorIcons
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreenRedesigned(
    viewModel: ChatViewModel = viewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val filePicker = rememberFilePicker { files ->
        viewModel.attachFiles(files)
    }

    LaunchedEffect(uiState.messages.size, uiState.isSending) {
        if (uiState.messages.isNotEmpty() || uiState.isSending) {
            listState.animateScrollToItem(0)
        }
    }

    uiState.error?.let { error ->
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    if (uiState.showModelPicker) {
        // TODO: ModelPickerSheet остается без изменений
        ModelPickerSheet(
            models = uiState.availableModels,
            currentModel = uiState.selectedModel,
            isLoading = uiState.isLoadingModels,
            onSelect = { viewModel.selectModel(it) },
            onDismiss = { viewModel.toggleModelPicker() }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            ChatTopAppBar(
                selectedModel = uiState.selectedModel,
                onBack = onBack,
                onSelectModel = { viewModel.toggleModelPicker() },
                isLoadingModels = uiState.isLoadingModels,
                onClearChat = {
                    // TODO: Implement clear chat confirmation dialog
                    scope.launch {
                        // viewModel.clearChat()
                        snackbarHostState.showSnackbar("TODO: Clear chat feature")
                    }
                },
                onExportChat = {
                    // TODO: Implement export chat
                    scope.launch {
                        snackbarHostState.showSnackbar("TODO: Export chat feature")
                    }
                },
                onSearchChat = {
                    // TODO: Implement search in chat
                    scope.launch {
                        snackbarHostState.showSnackbar("TODO: Search in chat feature")
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
            // Панель информации о модели
            uiState.selectedModel?.let { model ->
                ModelInfoPanel(
                    model = model,
                    onSelectModel = { viewModel.toggleModelPicker() },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Список сообщений или пустое состояние
            if (uiState.messages.isEmpty() && !uiState.isSending) {
                ChatEmptyState(
                    model = uiState.selectedModel,
                    onStartChat = {
                        // Focus on input field
                        // TODO: Implement focus management
                    },
                    modifier = Modifier.weight(1f)
                )
            } else {
                MessagesList(
                    messages = uiState.messages,
                    selectedModel = uiState.selectedModel,
                    isSending = uiState.isSending,
                    listState = listState,
                    scope = scope,
                    onRetry = { viewModel.retryFailedMessage(it) },
                    onDelete = { viewModel.deleteMessage(it.id) },
                    onCopy = { copyToClipboard(it.content) },
                    onEdit = { message ->
                        // TODO: Implement message editing
                        scope.launch {
                            snackbarHostState.showSnackbar("TODO: Edit message feature")
                        }
                    },
                    onReply = { message ->
                        // TODO: Implement message replying
                        scope.launch {
                            snackbarHostState.showSnackbar("TODO: Reply to message feature")
                        }
                    },
                    onPin = { message ->
                        // TODO: Implement message pinning
                        scope.launch {
                            snackbarHostState.showSnackbar("TODO: Pin message feature")
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Поле ввода сообщений
            ChatInput(
                message = uiState.messageInput,
                onMessageChange = { viewModel.updateMessageInput(it) },
                onSend = { viewModel.sendMessage() },
                onAttachFiles = { filePicker.launch() },
                attachedFiles = uiState.attachedFiles,
                onRemoveFile = { viewModel.removeFile(it) },
                isSending = uiState.isSending,
                canSend = viewModel.canSend(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopAppBar(
    selectedModel: AIModel?,
    onBack: () -> Unit,
    onSelectModel: () -> Unit,
    isLoadingModels: Boolean,
    onClearChat: () -> Unit,
    onExportChat: () -> Unit,
    onSearchChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    
    TopAppBar(
        title = {
            Column {
                if (selectedModel != null) {
                    Text(
                        text = selectedModel.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Чат",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(stringResource(Res.string.select_model))
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = VectorIcons.ArrowBack,
                    contentDescription = stringResource(Res.string.back)
                )
            }
        },
        actions = {
            // Кнопка поиска
            IconButton(
                onClick = onSearchChat,
                enabled = selectedModel != null
            ) {
                Icon(
                    imageVector = TODO("Add Search icon from compose-resources"),
                    contentDescription = stringResource(Res.string.search_in_chat)
                )
            }
            
            // Кнопка смены модели
            TextButton(
                onClick = onSelectModel,
                enabled = !isLoadingModels && selectedModel != null
            ) {
                if (isLoadingModels) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(Res.string.change_model),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            
            // Меню дополнительных действий
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    enabled = selectedModel != null
                ) {
                    Icon(
                        imageVector = TODO("Add MoreVert icon from compose-resources"),
                        contentDescription = stringResource(Res.string.more_options)
                    )
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.clear_chat)) },
                        onClick = {
                            onClearChat()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = TODO("Add DeleteSweep icon from compose-resources"),
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.export_chat)) },
                        onClick = {
                            onExportChat()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = TODO("Add Download icon from compose-resources"),
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
        ),
        modifier = modifier
    )
}

@Composable
private fun MessagesList(
    messages: List<ChatMessage>,
    selectedModel: AIModel?,
    isSending: Boolean,
    listState: LazyListState,
    scope: CoroutineScope,
    onRetry: (ChatMessage) -> Unit,
    onDelete: (ChatMessage) -> Unit,
    onCopy: (ChatMessage) -> Unit,
    onEdit: (ChatMessage) -> Unit,
    onReply: (ChatMessage) -> Unit,
    onPin: (ChatMessage) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Bottom),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp)
        ) {
            // Индикатор отправки
            if (isSending) {
                item(key = "sending_indicator") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            tonalElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = stringResource(Res.string.typing_indicator),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModelPickerSheet(
    models: List<AIModel>,
    currentModel: AIModel?,
    isLoading: Boolean,
    onSelect: (AIModel) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val selectedTags = remember { mutableStateListOf<String>() }

    // move from here and add filter like "Free"
    val allTags = remember(models) {
        models.flatMap { model ->
            model.inputModalities.map { "in:$it" } + model.outputModalities.map { "out:$it" }
        }.distinct().sorted()
    }

    val filteredModels = remember(models, searchQuery, selectedTags.toList()) {
        models.filter { model ->
            val matchesSearch = searchQuery.isBlank() ||
                    model.name.contains(searchQuery, ignoreCase = true) ||
                    model.id.contains(searchQuery, ignoreCase = true)

            val modelTags = model.inputModalities.map { "in:$it" } +
                    model.outputModalities.map { "out:$it" }

            val matchesTags = selectedTags.isEmpty() ||
                    selectedTags.all { it in modelTags }

            matchesSearch && matchesTags
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(Res.string.select_model),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text(stringResource(Res.string.search_models)) },
                singleLine = true,
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Text(stringResource(Res.string.close_x))
                        }
                    }
                }
            )

            if (allTags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allTags.forEach { tag ->
                        val isSelected = tag in selectedTags
                        val isInput = tag.startsWith("in:")
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) selectedTags.remove(tag)
                                else selectedTags.add(tag)
                            },
                            label = { Text(tag, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (isInput)
                                    MaterialTheme.colorScheme.tertiaryContainer
                                else
                                    MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = if (isInput)
                                    MaterialTheme.colorScheme.onTertiaryContainer
                                else
                                    MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredModels.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.no_models_found),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    text = "${filteredModels.size} models",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(filteredModels, key = { it.id }) { model ->
                        val isSelected = model.id == currentModel?.id
                        ModelListItem(
                            model = model,
                            isSelected = isSelected,
                            onClick = {
                                onSelect(model)
                                onDismiss()
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelListItem(
    model: AIModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = model.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                model.contextLength?.let { length ->
                    Text(
                        text = formatContextLength(length),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            ModelCapabilities(model)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = formatPricing(model.inputPrice, model.outputPrice))
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

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(tags) { tag ->
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

private fun formatContextLength(tokens: Long): String {
    return when {
        tokens >= 1_000_000 -> "${tokens / 1_000_000}M ctx"
        tokens >= 1_000 -> "${tokens / 1_000}K ctx"
        else -> "$tokens tokens"
    }
}

private fun formatPricing(inputPrice: Double?, outputPrice: Double?): String {
    val inputStr = inputPrice?.let { formatPricePerMillion(it) }
    val outputStr = outputPrice?.let { formatPricePerMillion(it) }
    return when {
        inputStr != null && outputStr != null -> "In: $inputStr | Out: $outputStr"
        inputStr != null -> "In: $inputStr"
        outputStr != null -> "Out: $outputStr"
        else -> ""
    }
}

private fun formatPricePerMillion(pricePerToken: Double): String {
    val perMillion = pricePerToken * 1_000_000
    return when {
        perMillion == 0.0 -> "Free"
        perMillion < 0.01 -> "$" + (perMillion * 1000).toInt() / 1000.0
        perMillion < 1.0 -> "$" + (perMillion * 100).toInt() / 100.0
        else -> "$" + (perMillion * 10).toInt() / 10.0
    }
}

            // Сообщения в обратном порядке (новые внизу)
            val reversedMessages = messages.asReversed()
            items(reversedMessages, key = { it.id }) { message ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    if (message.isFromUser) {
                        UserMessageCard(
                            message = message,
                            scope = scope,
                            onRetry = onRetry,
                            onDelete = onDelete,
                            onCopy = onCopy,
                            onEdit = onEdit,
                            onReply = onReply,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        AIMessageCard(
                            message = message,
                            modelName = selectedModel?.name ?: stringResource(Res.string.ai),
                            onCopy = onCopy,
                            onDelete = onDelete,
                            onReply = onReply,
                            onPin = onPin,
                            isPinned = false, // TODO: Implement pinned messages
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Кнопка прокрутки вниз
        AnimatedVisibility(
            visible = listState.firstVisibleItemIndex > 5,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = TODO("Add ArrowDownward icon from compose-resources"),
                    contentDescription = "Scroll to bottom"
                )
            }
        }
    }
}