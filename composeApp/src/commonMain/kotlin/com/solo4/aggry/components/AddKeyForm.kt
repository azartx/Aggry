package com.solo4.aggry.components

import aggry.composeapp.generated.resources.Res
import aggry.composeapp.generated.resources.add_api_key
import aggry.composeapp.generated.resources.api_key
import aggry.composeapp.generated.resources.cancel
import aggry.composeapp.generated.resources.click_to_add_api_key
import aggry.composeapp.generated.resources.enter_api_key_title
import aggry.composeapp.generated.resources.hide_key
import aggry.composeapp.generated.resources.key_add_new
import aggry.composeapp.generated.resources.key_name_placeholder
import aggry.composeapp.generated.resources.key_name_title
import aggry.composeapp.generated.resources.save_key
import aggry.composeapp.generated.resources.save_key_info
import aggry.composeapp.generated.resources.show_key
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.solo4.aggry.img.Add
import com.solo4.aggry.img.VectorIcons
import com.solo4.aggry.img.Visibility
import com.solo4.aggry.img.VisibilityOff
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddKeyForm(
    name: String,
    onNameChange: (String) -> Unit,
    key: String,
    onKeyChange: (String) -> Unit,
    onSave: () -> Unit,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isKeyVisible by remember { mutableStateOf(false) }
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            if (!isExpanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleExpanded() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = VectorIcons.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(Res.string.add_api_key),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(Res.string.click_to_add_api_key),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Icon(
                        imageVector = VectorIcons.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        Text(
                            text = stringResource(Res.string.key_add_new),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        OutlinedTextField(
                            value = name,
                            onValueChange = onNameChange,
                            label = { Text(stringResource(Res.string.key_name_title)) },
                            placeholder = { Text(stringResource(Res.string.key_name_placeholder)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = VectorIcons.Add,
                                    contentDescription = null
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = key,
                            onValueChange = onKeyChange,
                            label = { Text(stringResource(Res.string.api_key)) },
                            placeholder = { Text(stringResource(Res.string.enter_api_key_title)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = if (isKeyVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { isKeyVisible = !isKeyVisible }) {
                                    Icon(
                                        imageVector = if (isKeyVisible) VectorIcons.VisibilityOff
                                        else VectorIcons.Visibility,
                                        contentDescription = if (isKeyVisible) {
                                            stringResource(Res.string.hide_key)
                                        } else {
                                            stringResource(Res.string.show_key)
                                        }
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onToggleExpanded()
                                    onNameChange("")
                                    onKeyChange("")
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(Res.string.cancel))
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Button(
                                onClick = {
                                    onSave()
                                    onToggleExpanded()
                                },
                                modifier = Modifier.weight(1f),
                                enabled = name.isNotBlank() && key.isNotBlank()
                            ) {
                                Text(stringResource(Res.string.save_key))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = stringResource(Res.string.save_key_info),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}