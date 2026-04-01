package com.solo4.aggry.db

actual fun currentTimeMillis(): Long = kotlin.js.Date.now().toLong()
