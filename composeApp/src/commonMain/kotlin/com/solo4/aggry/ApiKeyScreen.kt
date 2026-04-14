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
import aggry.composeapp.generated.resources.add_keys_info
import aggry.composeapp.generated.resources.add_your_first_key_btn_title
import aggry.composeapp.generated.resources.api_keys
import aggry.composeapp.generated.resources.no_api_keys_yet
import aggry.composeapp.generated.resources.your_api_keys
import com.solo4.aggry.img.Key
import com.solo4.aggry.img.VectorIcons
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
                    imageVector = VectorIcons.Key,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(4.dp)
                )
            }
            
            GradientText(
                text = stringResource(Res.string.api_keys),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
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
        
        if (uiState.apiKeys.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                SectionTitle(
                    text = stringResource(Res.string.your_api_keys),
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
            EmptyState(
                icon = VectorIcons.Key,
                title = stringResource(Res.string.no_api_keys_yet),
                description = stringResource(Res.string.add_keys_info),
                primaryButtonText = stringResource(Res.string.add_your_first_key_btn_title),
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
