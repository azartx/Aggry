package com.solo4.aggry.components.chat

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solo4.aggry.data.AttachedFile
import com.solo4.aggry.data.ChatMessage
import com.solo4.aggry.data.MessageStatus
import com.solo4.aggry.data.formatFileSize
import aggry.composeapp.generated.resources.Res
import aggry.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.CoroutineScope

@Composable
fun UserMessageCard(
    message: ChatMessage,
    scope: CoroutineScope,
    onRetry: (ChatMessage) -> Unit,
    onDelete: (ChatMessage) -> Unit,
    onCopy: (ChatMessage) -> Unit,
    onEdit: (ChatMessage) -> Unit,
    onReply: (ChatMessage) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.End
    ) {
        // Заголовок с аватаркой и временем
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatTime(message.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 8.dp)
            )
            
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = TODO("Add Person icon from compose-resources"),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.Center)
                )
            }
            
            Text(
                text = stringResource(Res.string.you),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Карточка сообщения
        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 6.dp,
                bottomStart = 18.dp,
                bottomEnd = 18.dp
            ),
            color = MaterialTheme.colorScheme.primary,
            tonalElevation = 2.dp,
            modifier = Modifier
                .widthIn(max = 280.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 6.dp,
                        bottomStart = 18.dp,
                        bottomEnd = 18.dp
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {},
                        onLongClick = { showMenu = true }
                    )
            ) {
                // Вложения
                if (message.attachedFiles.isNotEmpty()) {
                    AttachedFilesSection(
                        files = message.attachedFiles,
                        isUser = true,
                        modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp)
                    )
                }
                
                // Текст сообщения
                if (message.content.isNotBlank()) {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }
                
                // Сгенерированные изображения
                if (message.generatedImages.isNotEmpty()) {
                    TODO("Implement generated images section with improved design")
                }
                
                // Статус сообщения
                MessageStatusIndicator(
                    status = message.status,
                    onRetry = { onRetry(message) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
                )
            }
        }
        
        // Кнопки действий
        AnimatedVisibility(
            visible = showMenu,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .height(32.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionButton(
                    icon = TODO("Add ContentCopy icon from compose-resources"),
                    text = stringResource(Res.string.copy),
                    onClick = {
                        onCopy(message)
                        showMenu = false
                    }
                )
                
                ActionButton(
                    icon = TODO("Add Edit icon from compose-resources"),
                    text = stringResource(Res.string.edit_message),
                    onClick = {
                        onEdit(message)
                        showMenu = false
                    }
                )
                
                ActionButton(
                    icon = TODO("Add Reply icon from compose-resources"),
                    text = stringResource(Res.string.reply),
                    onClick = {
                        onReply(message)
                        showMenu = false
                    }
                )
                
                ActionButton(
                    icon = TODO("Add Delete icon from compose-resources"),
                    text = stringResource(Res.string.delete),
                    onClick = {
                        onDelete(message)
                        showMenu = false
                    },
                    isDestructive = true
                )
                
                IconButton(
                    onClick = { showMenu = false },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = TODO("Add Close icon from compose-resources"),
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
    
    // Меню действий (альтернатива для длинного тапа)
    DropdownMenu(
        expanded = false, // Управляется через combinedClickable
        onDismissRequest = { showMenu = false }
    ) {
        // Это запасной вариант, основной UI - кнопки над сообщением
    }
}

@Composable
fun AttachedFilesSection(
    files: List<AttachedFile>,
    isUser: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.attachments),
            style = MaterialTheme.typography.labelSmall,
            color = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        files.forEach { file ->
            Surface(
                shape = MaterialTheme.shapes.small,
                color = if (isUser) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (file.isImage) TODO("Add Image icon from compose-resources")
                            else TODO("Add Description icon from compose-resources"),
                            contentDescription = null,
                            tint = if (isUser) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Text(
                            text = file.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isUser) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Text(
                        text = formatFileSize(file.bytes.size),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageStatusIndicator(
    status: MessageStatus,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        when (status) {
            MessageStatus.SENDING -> {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(12.dp),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.width(6.dp))
                
                Text(
                    text = stringResource(Res.string.sending),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                )
            }
            
            MessageStatus.SENT -> {
                Icon(
                    imageVector = TODO("Add Check icon from compose-resources"),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    modifier = Modifier.size(12.dp)
                )
                
                Spacer(modifier = Modifier.width(6.dp))
                
                Text(
                    text = stringResource(Res.string.sent),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                )
            }
            
            MessageStatus.FAILED -> {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                    modifier = Modifier.clickable(onClick = onRetry)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = TODO("Add ErrorOutline icon from compose-resources"),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(12.dp)
                        )
                        
                        Text(
                            text = stringResource(Res.string.failed),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        
                        Text(
                            text = "• ${stringResource(Res.string.retry)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            else -> {}
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .height(32.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = if (isDestructive) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatTime(timestamp: Long): String {
    // TODO: Implement proper time formatting
    return "12:30"
}