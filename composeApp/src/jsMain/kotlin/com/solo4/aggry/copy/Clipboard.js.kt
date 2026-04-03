package com.solo4.aggry.copy

import kotlinx.browser.window

actual fun copyToClipboard(text: String) {
    window.navigator.clipboard.writeText(text)
}
