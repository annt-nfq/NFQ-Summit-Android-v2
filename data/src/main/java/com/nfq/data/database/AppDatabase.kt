package com.nfq.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nfq.data.BuildConfig
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [EventEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(EventTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        fun buildDatabase(context: Context): AppDatabase {
            val userEnteredPassphrase = BuildConfig.PASS_PHRASE.toCharArray()
            val passphrase: ByteArray = SQLiteDatabase.getBytes(userEnteredPassphrase)
            val factory = SupportFactory(passphrase)
            return Room.databaseBuilder(context, AppDatabase::class.java, "nfq-summit-db")
                .openHelperFactory(factory)
                .build()
        }
    }
}