package com.solo4.aggry.save

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.solo4.aggry.log.LogLevel
import com.solo4.aggry.log.log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

actual suspend fun savePhotoToGallery(path: String, mimeType: String): Result<Unit> {
    return runCatching {
        log(LogLevel.INFO, "PhotoSaver", "savePhotoToGallery called: path=$path, mimeType=$mimeType")
        
        val context = com.solo4.aggry.AndroidContextHolder.context
            ?: error("AndroidContextHolder.context is null. Call init in MainActivity")

        val file = File(path)
        if (!file.exists()) error("File does not exist: $path")

        val finalMimeType = mimeType
        val displayName = file.name.substringAfterLast('/', file.name)
        val datePrefix = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val finalName = "${datePrefix}_$displayName"

        val relativeDir = "Download"

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, finalName)
            put(MediaStore.MediaColumns.MIME_TYPE, finalMimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativeDir)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val itemUri = context.contentResolver.insert(collection, values)
            ?: error("MediaStore insert returned null URI")

        context.contentResolver.openOutputStream(itemUri).use { out ->
            requireNotNull(out) { "OutputStream is null for $itemUri" }
            file.inputStream().use { input ->
                input.copyTo(out)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.MediaColumns.IS_PENDING, 0)
            context.contentResolver.update(itemUri, values, null, null)
        }

        log(LogLevel.INFO, "PhotoSaver", "Saved to gallery: uri=$itemUri")
    }.onFailure { e ->
        log(LogLevel.ERROR, "PhotoSaver", "Failed to save photo: ${e.message}", e)
    }
}
