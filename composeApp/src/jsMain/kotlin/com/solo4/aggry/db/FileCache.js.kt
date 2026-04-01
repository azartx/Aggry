package com.solo4.aggry.db

import com.solo4.aggry.data.AttachedFile

actual class FileCache {
    actual fun saveFile(file: AttachedFile): String {
        error("FileCache is not supported on JS target")
    }

    actual fun loadFile(cachedPath: String): AttachedFile? {
        return null
    }

    actual fun deleteFile(cachedPath: String) {}

    actual fun clearAll() {}
}
