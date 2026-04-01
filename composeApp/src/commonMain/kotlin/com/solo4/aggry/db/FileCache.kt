package com.solo4.aggry.db

import com.solo4.aggry.data.AttachedFile

expect class FileCache {
    fun saveFile(file: AttachedFile): String
    fun loadFile(cachedPath: String): AttachedFile?
    fun deleteFile(cachedPath: String)
    fun clearAll()
}
