package com.solo4.aggry

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import java.io.File
import javax.imageio.ImageIO

@Composable
actual fun CachedImage(
    path: String,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale
) {
    val imageBitmap = remember(path) {
        try {
            val file = File(path)
            if (file.exists()) {
                ImageIO.read(file)?.toComposeImageBitmap()
            } else null
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
