package com.solo4.aggry.components

import aggry.composeapp.generated.resources.Res
import aggry.composeapp.generated.resources.delete
import aggry.composeapp.generated.resources.edit
import aggry.composeapp.generated.resources.just_now
import aggry.composeapp.generated.resources.m_ago
import aggry.composeapp.generated.resources.more_options
import aggry.composeapp.generated.resources.n_messages
import aggry.composeapp.generated.resources.rename
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.solo4.aggry.data.Conversation
import com.solo4.aggry.img.ChatBubble
import com.solo4.aggry.img.Delete
import com.solo4.aggry.img.Edit
import com.solo4.aggry.img.Memory
import com.solo4.aggry.img.Message
import com.solo4.aggry.img.MoreVert
import com.solo4.aggry.img.Schedule
import com.solo4.aggry.img.VectorIcons
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

@Composable
fun ConversationCard(
    conversation: Conversation,
    onClick: () -> Unit,
    onEdit: (Conversation) -> Unit,
    onDelete: (Conversation) -> Unit,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        tonalElevation = if (isSelected) 4.dp else 2.dp,
        border = CardDefaults.outlinedCardBorder(enabled = isSelected)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = VectorIcons.ChatBubble,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                        Text(
                            text = conversation.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        // TODO: implement last message text
                        /*Text(
                            text = formatLastMessage(conversation),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp)
                        )*/
                    }
                }
                
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = VectorIcons.MoreVert,
                            contentDescription = stringResource(Res.string.more_options),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.rename)) },
                            onClick = {
                                onEdit(conversation)
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = VectorIcons.Edit,
                                    contentDescription = stringResource(Res.string.edit)
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(Res.string.delete),
                                    color = MaterialTheme.colorScheme.error)
                                   },
                            onClick = {
                                onDelete(conversation)
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = VectorIcons.Delete,
                                    contentDescription = stringResource(Res.string.delete),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetaInfoItem(
                        icon = VectorIcons.Schedule,
                        text = formatTimeAgo(conversation.updatedAt),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    MetaInfoItem(
                        icon = VectorIcons.Message,
                        text = stringResource(Res.string.n_messages, conversation.messageCount.toString()),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))
                
                ModelChip(modelName = conversation.modelName)
            }
        }
    }
}

@Composable
private fun MetaInfoItem(
    icon: ImageVector,
    text: String,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun ModelChip(modelName: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.height(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = VectorIcons.Memory,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(12.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = modelName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun formatTimeAgo(timestamp: Long): String {
    val now = Clock.System.now().toEpochMilliseconds()
    val diff = now - timestamp
    
    return when {
        diff < 60_000 -> stringResource(Res.string.just_now)
        diff < 3_600_000 -> stringResource(Res.string.m_ago, diff / 60_000)
        diff < 86_400_000 -> stringResource(Res.string.m_ago, diff / 3_600_000)
        diff < 2_592_000_000 -> stringResource(Res.string.m_ago, diff / 86_400_000)
        else -> stringResource(Res.string.m_ago, diff / 2_592_000_000)
    }
}