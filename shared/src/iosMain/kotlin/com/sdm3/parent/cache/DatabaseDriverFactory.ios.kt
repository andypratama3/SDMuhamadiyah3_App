package com.sdm3.parent.cache

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

private const val CACHE_DB_NAME = "sdm3-cache-v3.db"

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = SDM3Database.Schema,
            name = CACHE_DB_NAME,
        )
    }
}
