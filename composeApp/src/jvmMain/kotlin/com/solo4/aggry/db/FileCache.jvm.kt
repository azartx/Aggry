package com.solo4.aggry.db

import com.solo4.aggry.data.AttachedFile
import java.io.File

actual class FileCache {

    private val cacheDir: File
        get() = File(System.getProperty("user.home"), ".aggry/cache/attached_files").also { it.mkdirs() }

    actual fun saveFile(file: AttachedFile): String {
        val safeName = file.name.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val cached = File(cacheDir, "${file.hashCode()}_$safeName")
        cached.writeBytes(file.bytes)
        return cached.absolutePath
    }

    actual fun saveBytes(bytes: ByteArray, nameHint: String): String {
        val safeName = nameHint.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val cached = File(cacheDir, "${bytes.hashCode()}_$safeName")
        cached.writeBytes(bytes)
        return cached.absolutePath
    }

    actual fun loadFile(cachedPath: String): AttachedFile? {
        val file = File(cachedPath)
        if (!file.exists()) return null
        val name = file.name.substringAfter("_")
        val mimeType = guessMimeType(name)
        return AttachedFile(name = name, bytes = file.readBytes(), mimeType = mimeType)
    }

    actual fun loadBytes(cachedPath: String): ByteArray? {
        val file = File(cachedPath)
        return if (file.exists()) file.readBytes() else null
    }

    actual fun deleteFile(cachedPath: String) {
        File(cachedPath).delete()
    }

    actual fun clearAll() {
        cacheDir.deleteRecursively()
    }

    private fun guessMimeType(name: String): String {
        val ext = name.substringAfterLast('.', "").lowercase()
        return when (ext) {
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "pdf" -> "application/pdf"
            else -> "application/octet-stream"
        }
    }
}
