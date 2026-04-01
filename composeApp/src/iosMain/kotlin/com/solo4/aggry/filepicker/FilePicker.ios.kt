package com.solo4.aggry.filepicker

import androidx.compose.runtime.*
import com.solo4.aggry.data.AttachedFile
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.getBytes
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIViewController
import platform.UniformTypeIdentifiers.UTType
import platform.UniformTypeIdentifiers.UTTypeContent
import platform.darwin.NSObject

@Composable
actual fun rememberFilePicker(
    onFilesPicked: (List<AttachedFile>) -> Unit
): FilePickerLauncher {
    var showPicker by remember { mutableStateOf(false) }
    var hostVC by remember { mutableStateOf<UIViewController?>(null) }

    if (showPicker) {
        hostVC?.let { vc ->
            DocumentPickerView(
                hostVC = vc,
                onPicked = { files ->
                    showPicker = false
                    onFilesPicked(files)
                },
                onDismiss = { showPicker = false }
            )
        }
    }

    return remember {
        FilePickerLauncher(
            launch = {
                hostVC = getCurrentUIViewController()
                showPicker = true
            }
        )
    }
}

private fun getCurrentUIViewController(): UIViewController? {
    val scene = platform.UIKit.UIApplication.sharedApplication.connectedScenes.firstOrNull() as? platform.UIKit.UIWindowScene
    return scene?.keyWindow?.rootViewController?.topMostViewController()
}

private fun UIViewController.topMostViewController(): UIViewController {
    return when (this) {
        is platform.UIKit.UINavigationController -> visibleViewController?.topMostViewController() ?: this
        is platform.UIKit.UITabBarController -> selectedViewController?.topMostViewController() ?: this
        is platform.UIKit.UIViewController -> presentedViewController?.topMostViewController() ?: this
        else -> this
    }
}

@Composable
private fun DocumentPickerView(
    hostVC: UIViewController,
    onPicked: (List<AttachedFile>) -> Unit,
    onDismiss: () -> Unit
) {
    DisposableEffect(hostVC) {
        val delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
            override fun documentPicker(
                controller: UIDocumentPickerViewController,
                didPickDocumentsAtURLs: List<*>
            ) {
                val urls = didPickDocumentsAtURLs.filterIsInstance<NSURL>()
                val files = urls.mapNotNull { url ->
                    try {
                        val data = NSData.dataWithContentsOfURL(url) ?: return@mapNotNull null
                        val name = url.lastPathComponent ?: "unknown"
                        val mimeType = url.pathExtension?.let { ext ->
                            UTType.typeWithFilenameExtension(ext)?.preferredMIMEType
                        } ?: "application/octet-stream"
                        AttachedFile(
                            name = name,
                            bytes = data.toByteArray(),
                            mimeType = mimeType
                        )
                    } catch (_: Exception) {
                        null
                    }
                }
                onPicked(files)
            }

            override fun documentPickerWasCancelled(
                controller: UIDocumentPickerViewController
            ) {
                onDismiss()
            }
        }

        val picker = UIDocumentPickerViewController(
            forOpeningContentTypes = listOf(UTTypeContent),
            asCopy = true
        )
        picker.delegate = delegate
        hostVC.presentViewController(picker, animated = true, completion = null)

        onDispose {
            picker.dismissViewControllerAnimated(true, completion = null)
        }
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
