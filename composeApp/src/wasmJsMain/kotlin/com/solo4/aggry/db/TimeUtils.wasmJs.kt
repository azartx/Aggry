package com.solo4.aggry.db

import kotlin.time.TimeSource.Monotonic

actual fun currentTimeMillis(): Long = Monotonic.markNow().elapsedNow().inWholeMilliseconds
