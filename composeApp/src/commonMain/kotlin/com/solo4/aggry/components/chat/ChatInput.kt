package com.solo4.aggry.components.chat

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.solo4.aggry.data.AttachedFile
import com.solo4.aggry.data.formatFileSize
import aggry.composeapp.generated.resources.Res
import aggry.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInput(
    message: String,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    onAttachFiles: () -> Unit,
    attachedFiles: List<AttachedFile>,
    onRemoveFile: (Int) -> Unit,
    isSending: Boolean = false,
    canSend: Boolean = true,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    
    Column(modifier = modifier) {
        // Превью прикрепленных файлов
        if (attachedFiles.isNotEmpty()) {
            AttachedFilesPreview(
                files = attachedFiles,
                onRemove = onRemoveFile,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        
        // Основная область ввода
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column {
                // Поле ввода текста
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 8.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Кнопка прикрепления файлов
                    IconButton(
                        onClick = onAttachFiles,
                        modifier = Modifier.size(40.dp),
                        enabled = !isSending
                    ) {
                        Box {
                            Icon(
                                imageVector = TODO("Add AttachFile icon from compose-resources"),
                                contentDescription = stringResource(Res.string.attach_files),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            if (attachedFiles.isNotEmpty()) {
                                Badge(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 4.dp, y = (-4).dp),
                                    containerColor = MaterialTheme.colorScheme.primary
                                ) {
                                    Text(
                                        text = attachedFiles.size.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    }
                    
                    // Поле ввода текста
                    OutlinedTextField(
                        value = message,
                        onValueChange = onMessageChange,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        placeholder = { 
                            Text(
                                text = stringResource(Res.string.type_a_message),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        singleLine = false,
                        maxLines = 5,
                        minLines = 1,
                        keyboardOptions = KeyboardOptions(
                            autoCorrectEnabled = true,
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Send,
                            showKeyboardOnFocus = true
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (canSend && !isSending) {
                                    onSend()
                                }
                            }
                        ),
                        enabled = !isSending
                    )
                    
                    // Кнопка отправки
                    IconButton(
                        onClick = {
                            if (canSend && !isSending) {
                                onSend()
                                focusManager.clearFocus()
                            }
                        },
                        modifier = Modifier.size(48.dp),
                        enabled = canSend && !isSending
                    ) {
                        AnimatedContent(
                            targetState = isSending,
                            label = "send_button_state"
                        ) { sending ->
                            if (sending) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Surface(
                                    shape = CircleShape,
                                    color = if (canSend) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = TODO("Add Send icon from compose-resources"),
                                        contentDescription = stringResource(Res.string.send),
                                        tint = if (canSend) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Дополнительные кнопки
                AnimatedVisibility(
                    visible = message.isBlank() && attachedFiles.isEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // TODO: Добавить голосовые сообщения
                        /*
                        ActionChip(
                            onClick = { /* TODO: Voice message */ },
                            label = {
                                Text(
                                    text = stringResource(Res.string.voice_message),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = TODO("Add Mic icon from compose-resources"),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = ChipDefaults.actionChipColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        */
                        
                        // TODO: Добавить выбор эмодзи
                        /*
                        ActionChip(
                            onClick = { /* TODO: Emoji picker */ },
                            label = {
                                Text(
                                    text = stringResource(Res.string.emoji),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = TODO("Add EmojiEmotions icon from compose-resources"),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = ChipDefaults.actionChipColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        */
                    }
                }
            }
        }
    }
}

@Composable
private fun AttachedFilesPreview(
    files: List<AttachedFile>,
    onRemove: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.attachments),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(files) { index, file ->
                AttachedFileItem(
                    file = file,
                    onRemove = { onRemove(index) }
                )
            }
        }
    }
}

@Composable
private fun AttachedFileItem(
    file: AttachedFile,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .widthIn(max = 180.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = if (file.isImage) TODO("Add Image icon from compose-resources")
                else TODO("Add InsertDriveFile icon from compose-resources"),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = formatFileSize(file.bytes.size),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = TODO("Add Close icon from compose-resources"),
                    contentDescription = stringResource(Res.string.delete),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}