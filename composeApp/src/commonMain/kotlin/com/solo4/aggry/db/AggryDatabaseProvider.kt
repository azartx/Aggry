package com.solo4.aggry.db

object AggryDatabaseProvider {
    lateinit var database: AggryDatabase
        private set

    lateinit var fileCache: FileCache
        private set

    fun init(driverFactory: DatabaseDriverFactory, cache: FileCache) {
        database = AggryDatabase(driverFactory.createDriver())
        fileCache = cache
    }
}
