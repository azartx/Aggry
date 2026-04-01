package com.solo4.aggry.filepicker

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.solo4.aggry.data.AttachedFile

@Composable
actual fun rememberFilePicker(
    onFilesPicked: (List<AttachedFile>) -> Unit
): FilePickerLauncher {
    val context = LocalContext.current

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val files = uris.mapNotNull { uri ->
                try {
                    val name = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                        if (nameIndex >= 0 && cursor.moveToFirst()) cursor.getString(nameIndex) else null
                    } ?: uri.lastPathSegment ?: "unknown"

                    val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
                    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }

                    bytes?.let { AttachedFile(name = name, bytes = it, mimeType = mimeType) }
                } catch (_: Exception) {
                    null
                }
            }
            onFilesPicked(files)
        }
    }

    return remember {
        FilePickerLauncher(
            launch = {
                pickerLauncher.launch("*/*")
            }
        )
    }
}
