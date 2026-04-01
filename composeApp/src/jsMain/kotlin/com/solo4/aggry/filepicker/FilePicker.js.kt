package com.solo4.aggry.filepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.solo4.aggry.data.AttachedFile
import kotlinx.browser.document
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.asList
import org.w3c.files.File
import org.w3c.files.FileReader
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
actual fun rememberFilePicker(
    onFilesPicked: (List<AttachedFile>) -> Unit
): FilePickerLauncher {
    return remember {
        FilePickerLauncher(
            launch = {
                val input = document.createElement("input") as HTMLInputElement
                input.type = "file"
                input.multiple = true
                input.onchange = { event ->
                    val fileList = (event.target as? HTMLInputElement)?.files
                    if (fileList != null && fileList.length > 0) {
                        val jsFiles = fileList.asList()
                        val pending = jsFiles.size
                        var completed = 0
                        val results = mutableListOf<AttachedFile>()

                        jsFiles.forEach { file ->
                            val reader = FileReader()
                            reader.onload = { e ->
                                val arrayBuffer = e.target.asDynamic().result as? ArrayBuffer
                                if (arrayBuffer != null) {
                                    val bytes = arrayBuffer.toByteArray()
                                    val name = file.name
                                    val mimeType = file.type.ifEmpty { "application/octet-stream" }
                                    results.add(AttachedFile(name = name, bytes = bytes, mimeType = mimeType))
                                }
                                completed++
                                if (completed == pending && results.isNotEmpty()) {
                                    onFilesPicked(results)
                                }
                            }
                            reader.onerror = {
                                completed++
                                if (completed == pending && results.isNotEmpty()) {
                                    onFilesPicked(results)
                                }
                            }
                            reader.readAsArrayBuffer(file)
                        }
                    }
                }
                input.click()
            }
        )
    }
}

private fun ArrayBuffer.toByteArray(): ByteArray {
    val view = Int8Array(this)
    return ByteArray(view.length) { view[it] }
}
