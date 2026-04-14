package com.solo4.aggry.components.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.solo4.aggry.data.AIModel
import aggry.composeapp.generated.resources.Res
import aggry.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ModelInfoPanel(
    model: AIModel,
    onSelectModel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Заголовок с иконкой и названием модели
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = TODO("Add SmartToy icon from compose-resources"),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(22.dp)
                                .align(Alignment.Center)
                        )
                    }
                    
                    Column {
                        Text(
                            text = model.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            model.contextLength?.let { length ->
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                                    modifier = Modifier.height(20.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = TODO("Add Memory icon from compose-resources"),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        
                                        Text(
                                            text = formatContextLength(length),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                )
                            }
                        }
                            
                            PricingInfo(
                                inputPrice = model.inputPrice,
                                outputPrice = model.outputPrice
                            )
                        }
                    }
                }
                
                // Кнопка смены модели
                OutlinedButton(
                    onClick = onSelectModel,
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.change_model),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Возможности модели
            Text(
                text = stringResource(Res.string.model_capabilities),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            CapabilitiesRow(model = model)
        }
    }
}

@Composable
private fun PricingInfo(
    inputPrice: Double?,
    outputPrice: Double?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (inputPrice != null || outputPrice != null) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                modifier = Modifier.height(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = TODO("Add AttachMoney icon from compose-resources"),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(10.dp)
                    )
                    
                    Text(
                        text = formatPricingCompact(inputPrice, outputPrice),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun CapabilitiesRow(
    model: AIModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Input capabilities
        if (model.inputModalities.isNotEmpty()) {
            CapabilitySection(
                title = "Input",
                modalities = model.inputModalities,
                icon = TODO("Add Input icon from compose-resources"),
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        
        // Output capabilities
        if (model.outputModalities.isNotEmpty()) {
            CapabilitySection(
                title = "Output",
                modalities = model.outputModalities,
                icon = TODO("Add Output icon from compose-resources"),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CapabilitySection(
    title: String,
    modalities: List<String>,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        
        Text(
            text = "$title:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(modalities) { modality ->
            modalities.forEach { modality ->
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = color.copy(alpha = 0.1f),
                    modifier = Modifier.height(20.dp)
                ) {
                    Text(
                        text = modality,
                        style = MaterialTheme.typography.labelSmall,
                        color = color,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

private fun formatContextLength(tokens: Long): String {
    return when {
        tokens >= 1_000_000 -> "${tokens / 1_000_000}M ${stringResource(Res.string.tokens_label)}"
        tokens >= 1_000 -> "${tokens / 1_000}K ${stringResource(Res.string.tokens_label)}"
        else -> "$tokens ${stringResource(Res.string.tokens_label)}"
    }
}

private fun formatPricingCompact(inputPrice: Double?, outputPrice: Double?): String {
    val inputStr = inputPrice?.let { formatPricePerMillion(it) }
    val outputStr = outputPrice?.let { formatPricePerMillion(it) }
    
    return when {
        inputStr != null && outputStr != null -> "$inputStr/$outputStr"
        inputStr != null -> inputStr
        outputStr != null -> outputStr
        else -> stringResource(Res.string.free)
    }
}

private fun formatPricing(inputPrice: Double?, outputPrice: Double?): String {
    val inputStr = inputPrice?.let { 
        "${stringResource(Res.string.input_price_label)} ${formatPricePerMillion(it)}" 
    }
    val outputStr = outputPrice?.let { 
        "${stringResource(Res.string.output_price_label)} ${formatPricePerMillion(it)}" 
    }
    
    return when {
        inputStr != null && outputStr != null -> "$inputStr • $outputStr"
        inputStr != null -> inputStr
        outputStr != null -> outputStr
        else -> stringResource(Res.string.free)
    }
}

private fun formatPricePerMillion(pricePerToken: Double): String {
    val perMillion = pricePerToken * 1_000_000
    return when {
        perMillion == 0.0 -> stringResource(Res.string.free)
        perMillion < 0.01 -> "$${(perMillion * 1000).toInt() / 1000.0}"
        perMillion < 1.0 -> "$${(perMillion * 100).toInt() / 100.0}"
        else -> "$${(perMillion * 10).toInt() / 10.0}"
    }
}