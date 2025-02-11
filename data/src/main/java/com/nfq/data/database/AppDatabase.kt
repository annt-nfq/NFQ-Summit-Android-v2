package com.nfq.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nfq.data.BuildConfig
import com.nfq.data.database.dao.AttractionDao
import com.nfq.data.database.dao.BlogDao
import com.nfq.data.database.dao.EventDao
import com.nfq.data.database.dao.UserDao
import com.nfq.data.database.dao.VouchersDao
import com.nfq.data.database.entity.AttractionBlogEntity
import com.nfq.data.database.entity.AttractionEntity
import com.nfq.data.database.entity.BlogEntity
import com.nfq.data.database.entity.EventEntity
import com.nfq.data.database.entity.UserEntity
import com.nfq.data.database.entity.VoucherEntity
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [EventEntity::class, UserEntity::class, AttractionBlogEntity::class, AttractionEntity::class, BlogEntity::class, VoucherEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(EventTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun userDao(): UserDao
    abstract fun attractionDao(): AttractionDao
    abstract fun blogDao(): BlogDao
    abstract fun vouchersDao(): VouchersDao


    companion object {
        fun buildDatabase(context: Context): AppDatabase {
            val userEnteredPassphrase = BuildConfig.PASS_PHRASE.toCharArray()
            val passphrase: ByteArray = SQLiteDatabase.getBytes(userEnteredPassphrase)
            val factory = SupportFactory(passphrase)
            return Room.databaseBuilder(context, AppDatabase::class.java, "nfq-summit-2025")
                .openHelperFactory(factory)
                .build()
        }
    }
}
