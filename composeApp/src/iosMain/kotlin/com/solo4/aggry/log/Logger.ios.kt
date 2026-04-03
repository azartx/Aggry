package com.solo4.aggry.log

import platform.Foundation.NSLog

actual fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
    val prefix = when (level) {
        LogLevel.DEBUG -> "D"
        LogLevel.INFO -> "I"
        LogLevel.WARN -> "W"
        LogLevel.ERROR -> "E"
    }
    val text = if (throwable != null) {
        "$prefix: Aggry:$tag: $message\n${throwable.stackTraceToString()}"
    } else {
        "$prefix: Aggry:$tag: $message"
    }
    NSLog(text)
}
