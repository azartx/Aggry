package com.solo4.aggry

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile
import platform.UIKit.UIImage

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun CachedImage(
    path: String,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale
) {
    val imageBitmap = remember(path) {
        try {
            val nsData = NSData.dataWithContentsOfFile(path) ?: return@remember null
            val uiImage = UIImage(data = nsData) ?: return@remember null
            val cgImage = uiImage.CGImage ?: return@remember null
            org.jetbrains.skia.Image.makeFromCGImage(cgImage, uiImage.scale.toFloat())?.let {
                it.toComposeImageBitmap()
            }
        } catch (_: Exception) {
            null
        }
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}
