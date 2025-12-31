package com.segunfrancis.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        PhotosResponseEntity::class,
        UserEntity::class,
        UrlsEntity::class,
        UserProfileImageEntity::class,
        UserLinksEntity::class,
        LinksEntity::class
    ],
    version = 5,
    exportSchema = true
)
abstract class WDDatabase : RoomDatabase() {

    abstract fun getDao(): WDDao

    companion object {

        val migration_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE photo_response_new (id TEXT PRIMARY KEY NOT NULL, description TEXT, altDescription TEXT, blurHash TEXT, height INTEGER NOT NULL, width INTEGER NOT NULL, likes INTEGER NOT NULL)")
                database.execSQL("INSERT INTO photo_response_new (id, description, altDescription, blurHash, height, width, likes) SELECT id, description, altDescription, blurHash, height, width, likes FROM photo_response")
                database.execSQL("DROP TABLE photo_response")
                database.execSQL("ALTER TABLE photo_response_new RENAME TO photo_response")
            }
        }

        val migration_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE photo_response ADD COLUMN isFavourite INTEGER NOT NULL DEFAULT 0")
            }
        }

        val migration_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE photo_response ADD COLUMN category TEXT NOT NULL DEFAULT ''")
            }
        }

        val migration_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE links (photoId TEXT NOT NULL PRIMARY KEY, download TEXT NOT NULL, downloadLocation TEXT, FOREIGN KEY (photoId) REFERENCES photo_response (id) ON DELETE CASCADE)")
            }
        }

        @Volatile
        private var INSTANCE: WDDatabase? = null

        fun getDatabase(context: Context): WDDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    WDDatabase::class.java,
                    "WALLPAPER_DOWNLOADER_DATABASE"
                )
                    .addMigrations(migration_1_2)
                    .addMigrations(migration_2_3)
                    .addMigrations(migration_3_4)
                    .addMigrations(migration_4_5)
                    .build().also { INSTANCE = it }
            }
        }
    }
}
