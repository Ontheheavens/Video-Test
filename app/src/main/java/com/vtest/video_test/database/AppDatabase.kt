package com.vtest.video_test.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vtest.video_test.database.dao.VideoDAO
import com.vtest.video_test.database.entity.VideoEntity

@Database(entities = [VideoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun videoDao(): VideoDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        @Suppress("HardCodedStringLiteral")
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "video_db"
                ).enableMultiInstanceInvalidation()
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }

}