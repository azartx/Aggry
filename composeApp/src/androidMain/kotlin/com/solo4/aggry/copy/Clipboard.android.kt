package com.solo4.aggry.copy

import android.content.ClipData
import android.content.ClipboardManager
import androidx.core.content.getSystemService
import com.solo4.aggry.AndroidContextHolder

actual fun copyToClipboard(text: String) {
    val context = AndroidContextHolder.context ?: return
    val clipboard = context.getSystemService<ClipboardManager>() ?: return
    val clip = ClipData.newPlainText("text", text)
    clipboard.setPrimaryClip(clip)
}
