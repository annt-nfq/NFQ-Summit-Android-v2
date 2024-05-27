package com.nfq.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.nfq.data.cache.SummitDatabase

fun createDatabase(databaseDriver: SqlDriver) =
    SummitDatabase(
        databaseDriver
    )

fun createDriver(
    context: Context
): SqlDriver = AndroidSqliteDriver(
    SummitDatabase.Schema,
    context,
    "SummitDatabase"
)