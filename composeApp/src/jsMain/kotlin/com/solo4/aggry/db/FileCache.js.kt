package com.solo4.aggry.db

import com.solo4.aggry.data.AttachedFile

actual class FileCache {
    actual fun saveFile(file: AttachedFile): String {
        error("FileCache is not supported on JS target")
    }

    actual fun saveBytes(bytes: ByteArray, nameHint: String): String {
        error("FileCache is not supported on JS target")
    }

    actual fun loadFile(cachedPath: String): AttachedFile? {
        return null
    }

    actual fun loadBytes(cachedPath: String): ByteArray? {
        return null
    }

    actual fun deleteFile(cachedPath: String) {}

    actual fun clearAll() {}
}
