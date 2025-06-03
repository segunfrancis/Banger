package com.segunfrancis.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PhotosResponseEntity::class,
        UserEntity::class,
        UrlsEntity::class,
        UserProfileImageEntity::class,
        UserLinksEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WDDatabase : RoomDatabase() {

    abstract fun getDao(): WDDao

    companion object {

        @Volatile
        private var INSTANCE: WDDatabase? = null

        fun getDatabase(context: Context): WDDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    WDDatabase::class.java,
                    "WALLPAPER_DOWNLOADER_DATABASE"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
