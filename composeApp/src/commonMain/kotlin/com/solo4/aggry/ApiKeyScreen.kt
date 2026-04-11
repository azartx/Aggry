package com.solo4.aggry

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.solo4.aggry.data.ApiKey
import com.solo4.aggry.data.ApiKeyViewModel
import aggry.composeapp.generated.resources.Res
import aggry.composeapp.generated.resources.api_keys
import aggry.composeapp.generated.resources.key_name
import aggry.composeapp.generated.resources.api_key
import aggry.composeapp.generated.resources.save_key
import aggry.composeapp.generated.resources.no_api_keys
import aggry.composeapp.generated.resources.close_x
import org.jetbrains.compose.resources.stringResource

@Composable
fun ApiKeyScreen(
    viewModel: ApiKeyViewModel = viewModel(),
    onKeyClick: (ApiKey) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(Res.string.api_keys),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.nameInput,
                    onValueChange = { viewModel.updateNameInput(it) },
                    label = { Text(stringResource(Res.string.key_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.keyInput,
                    onValueChange = { viewModel.updateKeyInput(it) },
                    label = { Text(stringResource(Res.string.api_key)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.addKey(uiState.nameInput, uiState.keyInput)
                        viewModel.clearInputs()
                    },
                    modifier = Modifier.align(Alignment.End),
                    enabled = uiState.nameInput.isNotBlank() && uiState.keyInput.isNotBlank()
                ) {
                    Text(stringResource(Res.string.save_key))
                }
            }
        }

        if (uiState.apiKeys.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.no_api_keys),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.apiKeys, key = { it.id }) { apiKey ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onKeyClick(apiKey) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = apiKey.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = maskKey(apiKey.key),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { viewModel.removeKey(apiKey.id) }) {
                                Text(stringResource(Res.string.close_x))
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun maskKey(key: String): String {
    if (key.length <= 8) return "*".repeat(key.length)
    return key.take(4) + "*".repeat(key.length - 8) + key.takeLast(4)
}
