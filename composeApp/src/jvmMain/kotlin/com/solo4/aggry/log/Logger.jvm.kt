package com.solo4.aggry.log

actual fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
    val prefix = when (level) {
        LogLevel.DEBUG -> "DEBUG"
        LogLevel.INFO -> "INFO"
        LogLevel.WARN -> "WARN"
        LogLevel.ERROR -> "ERROR"
    }
    if (throwable != null) {
        System.err.println("[$prefix] Aggry:$tag: $message\n${throwable.stackTraceToString()}")
    } else {
        System.out.println("[$prefix] Aggry:$tag: $message")
    }
}
