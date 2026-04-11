package com.solo4.aggry

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.solo4.aggry.data.AIModel
import com.solo4.aggry.data.AttachedFile
import com.solo4.aggry.data.ChatMessage
import com.solo4.aggry.data.ChatViewModel
import com.solo4.aggry.data.formatFileSize
import com.solo4.aggry.data.MessageStatus
import com.solo4.aggry.save.savePhotoToGallery
import com.solo4.aggry.copy.copyToClipboard
import com.solo4.aggry.filepicker.rememberFilePicker
import com.solo4.aggry.img.arrowBack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import aggry.composeapp.generated.resources.Res
import aggry.composeapp.generated.resources.select_model
import aggry.composeapp.generated.resources.type_a_message
import aggry.composeapp.generated.resources.send
import aggry.composeapp.generated.resources.search_models
import aggry.composeapp.generated.resources.close_x
import aggry.composeapp.generated.resources.start_chatting
import aggry.composeapp.generated.resources.no_models_found
import aggry.composeapp.generated.resources.not_sent
import aggry.composeapp.generated.resources.tap_to_retry
import aggry.composeapp.generated.resources.copy
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val filePicker = rememberFilePicker { files ->
        viewModel.attachFiles(files)
    }

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

    if (uiState.showModelPicker) {
        ModelPickerSheet(
            models = uiState.availableModels,
            currentModel = uiState.selectedModel,
            isLoading = uiState.isLoadingModels,
            onSelect = { viewModel.selectModel(it) },
            onDismiss = { viewModel.toggleModelPicker() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (uiState.selectedModel?.name.isNullOrBlank()) {
                        Text(stringResource(Res.string.select_model))
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = arrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
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
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelLarge
                            )
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
                        text = stringResource(Res.string.start_chatting),
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
                        MessageBubble(message = message, scope, onRetry = { viewModel.retryFailedMessage(it) })
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

            if (uiState.attachedFiles.isNotEmpty()) {
                AttachedFilesRow(
                    files = uiState.attachedFiles,
                    onRemove = { viewModel.removeFile(it) }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    IconButton(onClick = { filePicker.launch() }) {
                        Text("\uD83D\uDCCE", fontSize = 20.sp)
                    }
                    if (uiState.attachedFiles.isNotEmpty()) {
                        Badge(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp),
                            containerColor = MaterialTheme.colorScheme.error
                        ) {
                            Text(uiState.attachedFiles.size.toString())
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.messageInput,
                    onValueChange = { viewModel.updateMessageInput(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(Res.string.type_a_message)) },
                    singleLine = false,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { viewModel.sendMessage() },
                    enabled = viewModel.canSend()
                ) {
                    Text(stringResource(Res.string.send))
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
                .padding(horizontal = 16.dp, vertical = 10.dp)
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
private fun AttachedFilesRow(
    files: List<AttachedFile>,
    onRemove: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        itemsIndexed(files) { index, file ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = file.name,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 100.dp)
                    )
                    Text(
                        text = formatFileSize(file.bytes.size),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        modifier = Modifier.size(16.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.error,
                        onClick = { onRemove(index) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "x",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onError,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage, scope: CoroutineScope, onRetry: (ChatMessage) -> Unit) {
    val isUser = message.isFromUser
    var menuExpanded by remember(message.id) { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(12.dp)
                .combinedClickable(
                    onClick = {},
                    onLongClick = { menuExpanded = true }
                )
        ) {
            if (!isUser && message.status == MessageStatus.FAILED) {
                // failed incoming messages aren't retryable
            }
            if (isUser && message.status == MessageStatus.FAILED) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRetry(message) }
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = stringResource(Res.string.not_sent),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(Res.string.tap_to_retry),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            if (message.attachedFiles.isNotEmpty()) {
                message.attachedFiles.forEach { file ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isUser) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (file.isImage) "\uD83D\uDDBC" else "\uD83D\uDCC4",
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = file.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isUser) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = formatFileSize(file.bytes.size),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
            if (message.content.isNotBlank()) {
                if (isUser) {
                    Text(
                        text = message.content,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Markdown(
                        message.content,
                        colors = markdownColor(text = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
            if (message.generatedImages.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                message.generatedImages.forEach { image ->
                    var viewerOpen by remember(image.cachedPath) { mutableStateOf(false) }
                    if (viewerOpen) {
                        PhotoViewer(
                            image = image,
                            onDismiss = { viewerOpen = false },
                            onDownload = {
                                scope.launch {
                                    savePhotoToGallery(image.cachedPath, image.mimeType)
                                }
                            }
                        )
                    }

                    CachedImage(
                        path = image.cachedPath,
                        contentDescription = "Generated image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewerOpen = true },
                        contentScale = ContentScale.FillWidth
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        androidx.compose.material3.DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            androidx.compose.material3.DropdownMenuItem(
                text = { Text(stringResource(Res.string.copy)) },
                onClick = {
                    menuExpanded = false
                    copyToClipboard(message.content)
                }
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
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = formatPricing(model.inputPrice, model.outputPrice))
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
