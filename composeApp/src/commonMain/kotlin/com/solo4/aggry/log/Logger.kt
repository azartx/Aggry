package com.solo4.aggry.log

enum class LogLevel { DEBUG, INFO, WARN, ERROR }

expect fun log(level: LogLevel, tag: String, message: String, throwable: Throwable? = null)
