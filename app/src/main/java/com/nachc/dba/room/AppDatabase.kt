package com.nachc.dba.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nachc.dba.models.Favourite

@Database(entities = [Favourite::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val favouriteDao: FavouriteDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            /**
             * Wrapping the code to get the database into synchronized means that only
             * one thread of execution at a time can enter this block of code,
             * which makes sure the database only gets initialized once.
             * */
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database")
                    .fallbackToDestructiveMigration()
                    .build().also { instance = it }
            }
        }
    }
}