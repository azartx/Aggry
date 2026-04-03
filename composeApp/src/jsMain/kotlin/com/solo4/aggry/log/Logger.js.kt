package com.solo4.aggry.log

actual fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
    // JS target: console logging
    val prefix = when (level) {
        LogLevel.DEBUG -> "DEBUG"
        LogLevel.INFO -> "INFO"
        LogLevel.WARN -> "WARN"
        LogLevel.ERROR -> "ERROR"
    }
    if (throwable != null) {
        println("[$prefix] Aggry:$tag: $message\n$throwable")
    } else {
        println("[$prefix] Aggry:$tag: $message")
    }
}
