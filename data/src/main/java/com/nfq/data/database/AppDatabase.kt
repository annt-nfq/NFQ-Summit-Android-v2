package com.nfq.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nfq.data.BuildConfig
import com.nfq.data.database.dao.AttractionDao
import com.nfq.data.database.dao.BlogDao
import com.nfq.data.database.dao.EventDao
import com.nfq.data.database.dao.UserDao
import com.nfq.data.database.dao.VouchersDao
import com.nfq.data.database.entity.AttractionEntity
import com.nfq.data.database.entity.AttractionBlogEntity
import com.nfq.data.database.entity.BlogEntity
import com.nfq.data.database.entity.EventEntity
import com.nfq.data.database.entity.UserEntity
import com.nfq.data.database.entity.VoucherEntity
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [EventEntity::class, UserEntity::class, AttractionBlogEntity::class, AttractionEntity::class, BlogEntity::class, VoucherEntity::class],
    version = 3,
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
            return Room.databaseBuilder(context, AppDatabase::class.java, "nfq-summit-db")
                .openHelperFactory(factory)
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
        }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add the new column to the event_entity table
        db.execSQL("ALTER TABLE event_entity ADD COLUMN isTechRock INTEGER DEFAULT 0 NOT NULL")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS voucher_entity (
                id TEXT NOT NULL,
                type TEXT NOT NULL,
                date TEXT NOT NULL,
                location TEXT NOT NULL,
                price TEXT NOT NULL,
                imageUrl TEXT NOT NULL,
                sponsorLogoUrls TEXT NOT NULL,
                PRIMARY KEY(id)
            )
        """
        )
    }
}