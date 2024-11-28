package com.colab.myfriend.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.colab.myfriend.adapter.FriendDao

@Database(
    entities = [Friend::class],
    version = 4,
    exportSchema = true, // Wajib untuk auto-migration
    autoMigrations = [
        AutoMigration(from = 3, to = 4)
    ]
)
abstract class MyDatabase : RoomDatabase() {
    abstract fun friendDao(): FriendDao

    companion object {
        @Volatile
        private var INSTANCE: MyDatabase? = null

        fun getInstance(context: Context): MyDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    "my_database"
                )
                    .build()
            }
        }
    }
}
