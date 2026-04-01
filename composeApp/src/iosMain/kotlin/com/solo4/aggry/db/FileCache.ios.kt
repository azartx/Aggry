package com.solo4.aggry.db

import com.solo4.aggry.data.AttachedFile
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.getBytes
import platform.Foundation.writeToFile

actual class FileCache {

    @OptIn(ExperimentalForeignApi::class)
    private val cacheDir: String
        get() {
            val urls = NSFileManager.defaultManager.URLsForDirectory(
                NSCachesDirectory,
                NSUserDomainMask
            )
            val cachesUrl = urls.first() as NSURL
            val dir = cachesUrl.path + "/attached_files"
            NSFileManager.defaultManager.createDirectoryAtPath(
                dir,
                withIntermediateDirectories = true,
                attributes = null,
                error = null
            )
            return dir
        }

    actual fun saveFile(file: AttachedFile): String {
        val safeName = file.name.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val path = "$cacheDir/${file.hashCode()}_$safeName"
        val nsData = file.bytes.toNSData()
        nsData.writeToFile(path, atomically = true)
        return path
    }

    actual fun loadFile(cachedPath: String): AttachedFile? {
        val data = NSData.dataWithContentsOfFile(cachedPath) ?: return null
        val name = cachedPath.substringAfterLast("_")
        val mimeType = guessMimeType(name)
        return AttachedFile(name = name, bytes = data.toByteArray(), mimeType = mimeType)
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun deleteFile(cachedPath: String) {
        NSFileManager.defaultManager.removeItemAtPath(cachedPath, error = null)
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun clearAll() {
        NSFileManager.defaultManager.removeItemAtPath(cacheDir, error = null)
        NSFileManager.defaultManager.createDirectoryAtPath(
            cacheDir,
            withIntermediateDirectories = true,
            attributes = null,
            error = null
        )
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

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData {
    if (isEmpty()) return NSData()
    return this.usePinned { pinned ->
        NSData.dataWithBytes(pinned.addressOf(0), size.toULong())
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = this.length.toInt()
    if (size == 0) return ByteArray(0)
    return ByteArray(size).also { array ->
        array.usePinned { pinned ->
            this.getBytes(pinned.addressOf(0), this.length)
        }
    }
}
