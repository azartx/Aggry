package com.solo4.aggry.db

import com.solo4.aggry.data.AttachedFile

expect class FileCache {
    fun saveFile(file: AttachedFile): String
    fun saveBytes(bytes: ByteArray, nameHint: String): String
    fun loadFile(cachedPath: String): AttachedFile?
    fun loadBytes(cachedPath: String): ByteArray?
    fun deleteFile(cachedPath: String)
    fun clearAll()
}
