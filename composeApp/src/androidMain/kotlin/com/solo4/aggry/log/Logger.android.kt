package com.solo4.aggry.log

import android.util.Log

actual fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
    val prefix = when (level) {
        LogLevel.DEBUG -> "D"
        LogLevel.INFO -> "I"
        LogLevel.WARN -> "W"
        LogLevel.ERROR -> "E"
    }
    val fullTag = "Aggry:$tag"
    when (level) {
        LogLevel.DEBUG -> Log.d(fullTag, prefix + ": " + message, throwable)
        LogLevel.INFO -> Log.i(fullTag, prefix + ": " + message, throwable)
        LogLevel.WARN -> Log.w(fullTag, prefix + ": " + message, throwable)
        LogLevel.ERROR -> Log.e(fullTag, prefix + ": " + message, throwable)
    }
}
