package com.solo4.aggry.filepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.solo4.aggry.data.AttachedFile
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.net.URLConnection

@Composable
actual fun rememberFilePicker(
    onFilesPicked: (List<AttachedFile>) -> Unit
): FilePickerLauncher {
    return remember {
        FilePickerLauncher(
            launch = {
                val dialog = FileDialog(null as Frame?, "Select File", FileDialog.LOAD)
                dialog.isMultipleMode = true
                dialog.isVisible = true

                val files = dialog.files?.mapNotNull { file: File ->
                    try {
                        val mimeType = URLConnection.guessContentTypeFromName(file.name)
                            ?: "application/octet-stream"
                        AttachedFile(
                            name = file.name,
                            bytes = file.readBytes(),
                            mimeType = mimeType
                        )
                    } catch (_: Exception) {
                        null
                    }
                } ?: emptyList()

                if (files.isNotEmpty()) {
                    onFilesPicked(files)
                }
            }
        )
    }
}
