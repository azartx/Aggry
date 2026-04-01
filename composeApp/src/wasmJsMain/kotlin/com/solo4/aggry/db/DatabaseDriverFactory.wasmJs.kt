package com.solo4.aggry.db

import app.cash.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        error("SQLDelight is not supported on WasmJS target")
    }
}
