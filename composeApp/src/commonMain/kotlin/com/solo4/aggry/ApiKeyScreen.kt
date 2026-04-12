package com.solo4.aggry

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.solo4.aggry.components.*
import com.solo4.aggry.data.ApiKey
import com.solo4.aggry.data.ApiKeyViewModel
import aggry.composeapp.generated.resources.Res
import aggry.composeapp.generated.resources.api_keys
import aggry.composeapp.generated.resources.key_name
import aggry.composeapp.generated.resources.api_key
import aggry.composeapp.generated.resources.save_key
import aggry.composeapp.generated.resources.no_api_keys
import aggry.composeapp.generated.resources.close_x
import com.solo4.aggry.img.ArrowBack
import org.jetbrains.compose.resources.stringResource

@Composable
fun ApiKeyScreen(
    viewModel: ApiKeyViewModel = viewModel(),
    onKeyClick: (ApiKey) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var isAddFormExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Заголовок с градиентом
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = ArrowBack,//TODO("Add Key icon from compose-resources"),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.CenterVertically)
                )
            }
            
            GradientText(
                text = stringResource(Res.string.api_keys),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Форма добавления ключа
        AddKeyForm(
            name = uiState.nameInput,
            onNameChange = { viewModel.updateNameInput(it) },
            key = uiState.keyInput,
            onKeyChange = { viewModel.updateKeyInput(it) },
            onSave = {
                viewModel.addKey(uiState.nameInput, uiState.keyInput)
                viewModel.clearInputs()
            },
            isExpanded = isAddFormExpanded,
            onToggleExpanded = { isAddFormExpanded = !isAddFormExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )
        
        // Секция с существующими ключами
        if (uiState.apiKeys.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                SectionTitle(
                    text = "Your API Keys",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.apiKeys, key = { it.id }) { apiKey ->
                        AnimatedApiKeyItem(
                            apiKey = apiKey,
                            onClick = { onKeyClick(apiKey) },
                            onEdit = { /* Редактирование будет добавлено позже */ },
                            onDelete = { viewModel.removeKey(apiKey.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        } else {
            // Empty state с иллюстрацией
            EmptyState(
                icon = ArrowBack,//TODO("Add Key icon from compose-resources"),
                title = "No API Keys Yet",
                description = "Add your first API key to start using Aggry. Your keys are stored securely and only used for your requests.",
                primaryButtonText = "Add Your First Key",
                onPrimaryClick = { isAddFormExpanded = true },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 48.dp)
            )
        }
    }
}

@Composable
private fun AnimatedApiKeyItem(
    apiKey: ApiKey,
    onClick: (ApiKey) -> Unit,
    onEdit: (ApiKey) -> Unit,
    onDelete: (ApiKey) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        ApiKeyCard(
            apiKey = apiKey,
            onClick = onClick,
            onEdit = onEdit,
            onDelete = onDelete,
            modifier = modifier
        )
    }
}
