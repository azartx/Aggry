package com.solo4.aggry.log

actual fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
    val prefix = when (level) {
        LogLevel.DEBUG -> "DEBUG"
        LogLevel.INFO -> "INFO"
        LogLevel.WARN -> "WARN"
        LogLevel.ERROR -> "ERROR"
    }
    if (throwable != null) {
        console.log("[$prefix] Aggry:$tag: $message", throwable)
    } else {
        console.log("[$prefix] Aggry:$tag: $message")
    }
}
