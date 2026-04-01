package com.solo4.aggry.filepicker

import androidx.compose.runtime.Composable
import com.solo4.aggry.data.AttachedFile

data class FilePickerLauncher(
    val launch: () -> Unit
)

@Composable
expect fun rememberFilePicker(
    onFilesPicked: (List<AttachedFile>) -> Unit
): FilePickerLauncher
