package com.colab.myfriend.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.colab.myfriend.adapter.FriendDao

@Database(
    entities = [Friend::class],
    version = 7,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 6, to = 7, spec = FriendAutoMigrationSpec::class)
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