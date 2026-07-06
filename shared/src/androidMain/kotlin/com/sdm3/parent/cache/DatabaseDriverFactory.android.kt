package com.sdm3.parent.cache

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

private const val CACHE_DB_NAME = "sdm3-cache-v3.db"

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = SDM3Database.Schema,
            context = context,
            name = CACHE_DB_NAME,
        )
    }
}
