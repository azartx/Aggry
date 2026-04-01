package com.solo4.aggry.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val dbPath = File(System.getProperty("user.home"), ".aggry/aggry.db")
        dbPath.parentFile.mkdirs()
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbPath.absolutePath}")
        AggryDatabase.Schema.create(driver)
        return driver
    }
}
