package com.solo4.aggry.components.chat

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.solo4.aggry.data.AIModel
import aggry.composeapp.generated.resources.Res
import aggry.composeapp.generated.resources.*
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import com.solo4.aggry.img.ArrowForward
import com.solo4.aggry.img.AutoStories
import com.solo4.aggry.img.ChatBubbleOutline
import com.solo4.aggry.img.Code
import com.solo4.aggry.img.Lightbulb
import com.solo4.aggry.img.Translate
import com.solo4.aggry.img.VectorIcons
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChatEmptyState(
    model: AIModel?,
    onStartChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(120.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = VectorIcons.ChatBubbleOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    AnimatedFloatingElements()
                }
            }
        }
        
        Text(
            text = if (model != null) {
                "Начните общение с ${model.name}"
            } else {
                stringResource(Res.string.start_chatting)
            },
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .padding(bottom = 16.dp)
        )
        
        Text(
            text = stringResource(Res.string.type_to_start_chatting),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 48.dp)
                .padding(bottom = 32.dp)
        )
        
        if (model != null) {
            ExamplePromptsSection(
                modelName = model.name,
                onPromptClick = { prompt ->
                    // TODO: Handle prompt selection
                }
            )
        }
        
        Text(
            text = "💡 Совет: Нажмите Enter для отправки сообщения",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .padding(top = 24.dp)
        )
    }
}

@Composable
private fun AnimatedFloatingElements() {
    // TODO: Add animated floating elements around the main icon
    // This would require animation APIs and is a placeholder for now
    Box(
        modifier = Modifier
            .size(180.dp)
            //.align(Alignment.Center)
    ) {
        // Floating element 1
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = VectorIcons.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.Center)
            )
        }
        
        // Floating element 2
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = VectorIcons.Code,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.Center)
            )
        }
        
        // Floating element 3
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.BottomStart)
        ) {
            Icon(
                imageVector = VectorIcons.Translate,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.Center)
            )
        }
        
        // Floating element 4
        Surface(
            shape = MaterialTheme.shapes.small,
            color = Color(0xFF004D27).copy(alpha = 0.7f), //successContainer TODO: theme
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = VectorIcons.AutoStories,
                contentDescription = null,
                tint = Color(0xFF81C995),// TODO: success color,
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun ExamplePromptsSection(
    modelName: String,
    onPromptClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Примеры для начала:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        val examplePrompts = listOf(
            "Объясни это как для новичка",
            "Напиши код для...",
            "Переведи на английский",
            "Создай план для..."
        )
        
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            examplePrompts.forEach { prompt ->
                ExamplePromptCard(
                    prompt = prompt,
                    onClick = { onPromptClick(prompt) }
                )
            }
        }
    }
}

@Composable
private fun ExamplePromptCard(
    prompt: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = prompt,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            
            Icon(
                imageVector = VectorIcons.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}