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
            return Room.databaseBuilder(context, AppDatabase::class.java, "nfq-summit-2025")
                .openHelperFactory(factory)
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .build()
        }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Create a new table with the updated schema
        db.execSQL(
            """
            CREATE TABLE voucher_entity_new (
                id TEXT NOT NULL,
                type TEXT NOT NULL,
                date TEXT NOT NULL,
                locations TEXT NOT NULL,
                price TEXT NOT NULL,
                imageUrl TEXT NOT NULL,
                sponsorLogoUrls TEXT NOT NULL,
                PRIMARY KEY(id)
            )
        """.trimIndent()
        )

        // Copy the data from the old table to the new table
        db.execSQL(
            """
            INSERT INTO voucher_entity_new (id, type, date, locations, price, imageUrl, sponsorLogoUrls)
            SELECT id, type, date, '[]', price, imageUrl, sponsorLogoUrls
            FROM voucher_entity
        """.trimIndent()
        )

        // Remove the old table
        db.execSQL("DROP TABLE voucher_entity")

        // Rename the new table to the old table name
        db.execSQL("ALTER TABLE voucher_entity_new RENAME TO voucher_entity")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE event_entity ADD COLUMN locations TEXT NOT NULL DEFAULT '[]'")
    }
}
