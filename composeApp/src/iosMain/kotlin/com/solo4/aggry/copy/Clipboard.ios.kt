package com.solo4.aggry.copy

import platform.Foundation.NSString
import platform.UIKit.UIPasteboard

actual fun copyToClipboard(text: String) {
    UIPasteboard.generalPasteboard.string = text
}
