package com.nfq.data.local

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
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
    schema = SummitDatabase.Schema,
    context = context,
    name = "SummitDatabase",
    callback = object: AndroidSqliteDriver.Callback(SummitDatabase.Schema) {
        override fun onOpen(db: SupportSQLiteDatabase) {
            db.setForeignKeyConstraintsEnabled(true)
        }
    }
)