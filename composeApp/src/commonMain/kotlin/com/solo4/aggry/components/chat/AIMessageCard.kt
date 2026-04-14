package com.solo4.aggry.components.chat

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.solo4.aggry.data.ChatMessage
import aggry.composeapp.generated.resources.Res
import aggry.composeapp.generated.resources.*
import com.solo4.aggry.img.Close
import com.solo4.aggry.img.ContentCopy
import com.solo4.aggry.img.Delete
import com.solo4.aggry.img.Image
import com.solo4.aggry.img.PushPin
import com.solo4.aggry.img.PushPinFilled
import com.solo4.aggry.img.Reply
import com.solo4.aggry.img.SmartToy
import com.solo4.aggry.img.VectorIcons
import org.jetbrains.compose.resources.stringResource

@Composable
fun AIMessageCard(
    message: ChatMessage,
    modelName: String,
    onCopy: (ChatMessage) -> Unit,
    onDelete: (ChatMessage) -> Unit,
    onReply: (ChatMessage) -> Unit,
    onPin: (ChatMessage) -> Unit,
    isPinned: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
//    var showImagePreview by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = VectorIcons.SmartToy,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.CenterVertically)
                )
            }
            
            Text(
                text = modelName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
            
            Text(
                text = "Mocked timestamp",//TODO: formatTime(message.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp)
            )
            
            if (isPinned) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = VectorIcons.PushPin,
                    contentDescription = "Pinned",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Surface(
            shape = RoundedCornerShape(
                topStart = 6.dp,
                topEnd = 18.dp,
                bottomStart = 18.dp,
                bottomEnd = 18.dp
            ),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier
                .widthIn(max = 300.dp)
                .combinedClickable(
                    onClick = {},
                    onLongClick = { showMenu = true }
                )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (message.attachedFiles.isNotEmpty()) {
                    AttachedFilesSection(
                        files = message.attachedFiles,
                        isUser = false,
                        modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp)
                    )
                }
                
                if (message.content.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Markdown(
                            content = message.content,
                            colors = markdownColor(
                                text = MaterialTheme.colorScheme.onSurface,
                                //codeText = MaterialTheme.colorScheme.primary,
                                codeBackground = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                if (message.generatedImages.isNotEmpty()) {
                    GeneratedImagesSection(
                        images = message.generatedImages,
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                    )
                }
            }
        }
        
        AnimatedVisibility(
            visible = showMenu,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .height(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionButton(
                    icon = VectorIcons.ContentCopy,
                    text = stringResource(Res.string.copy),
                    onClick = {
                        onCopy(message)
                        showMenu = false
                    }
                )
                
                ActionButton(
                    icon = VectorIcons.Reply,
                    text = stringResource(Res.string.reply),
                    onClick = {
                        onReply(message)
                        showMenu = false
                    }
                )
                
                ActionButton(
                    icon = if (isPinned) {
                        VectorIcons.PushPin
                    } else {
                        VectorIcons.PushPinFilled
                    },
                    text = if (isPinned) {
                        stringResource(Res.string.unpin)
                    } else {
                        stringResource(Res.string.pin)
                    },
                    onClick = {
                        onPin(message)
                        showMenu = false
                    }
                )
                
                ActionButton(
                    icon = VectorIcons.Delete,
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
                        imageVector = VectorIcons.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
    
    // TODO: Implement image preview dialog
//    if (showImagePreview) {
        // ImagePreviewDialog(...)
//    }
}

@Composable
private fun GeneratedImagesSection(
    images: List<com.solo4.aggry.data.GeneratedImage>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.generated_image),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        images.forEach { image ->
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable {
                        // TODO: Open image viewer
                        // showImagePreview = true
                    }
            ) {
                Column {
                    // TODO: Replace with actual image preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = VectorIcons.Image,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(Res.string.tap_to_view_image),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        // TODO: Add download button
                        /*
                        IconButton(
                            onClick = {
                                scope.launch {
                                    savePhotoToGallery(image.cachedPath, image.mimeType)
                                }
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = TODO("Add Download icon from compose-resources"),
                                contentDescription = stringResource(Res.string.download),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        */
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
    return "12:31"
}