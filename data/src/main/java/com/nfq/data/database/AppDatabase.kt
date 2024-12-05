package com.nfq.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nfq.data.BuildConfig
import com.nfq.data.database.dao.EventDao
import com.nfq.data.database.dao.UserDao
import com.nfq.data.database.entity.EventEntity
import com.nfq.data.database.entity.UserEntity
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [EventEntity::class, UserEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(EventTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun userDao(): UserDao

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