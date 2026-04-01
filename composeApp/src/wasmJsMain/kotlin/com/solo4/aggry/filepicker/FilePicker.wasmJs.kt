package com.solo4.aggry.filepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.solo4.aggry.data.AttachedFile
import kotlinx.serialization.json.Json

@OptIn(ExperimentalWasmJsInterop::class)
@Composable
actual fun rememberFilePicker(
    onFilesPicked: (List<AttachedFile>) -> Unit
): FilePickerLauncher {
    return remember {
        FilePickerLauncher(
            launch = {
                pickFiles { files ->
                    onFilesPicked(
                        files.map {
                            Json.decodeFromString(it.toString()) // TODO: check this, might not worked
                        }
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalWasmJsInterop::class)
private fun pickFiles(callback: (Array<*>) -> Unit) {
    js(
        """(function(callback) {
        var input = document.createElement('input');
        input.type = 'file';
        input.multiple = true;
        input.onchange = function(event) {
            var files = event.target.files;
            if (!files || files.length === 0) return;
            var results = [];
            var pending = files.length;
            var completed = 0;
            for (var i = 0; i < files.length; i++) {
                (function(file) {
                    var reader = new FileReader();
                    reader.onload = function(e) {
                        var bytes = new Int8Array(e.target.result);
                        results.push({ name: file.name, bytes: bytes, mimeType: file.type || 'application/octet-stream' });
                        completed++;
                        if (completed === pending) callback(results);
                    };
                    reader.onerror = function() {
                        completed++;
                        if (completed === pending) callback(results);
                    };
                    reader.readAsArrayBuffer(file);
                })(files[i]);
            }
        };
        input.click();
    })"""
    )
}
