package com.unsyiah.timemaster.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TimeMasterEvent::class], version = 1, exportSchema = false)
public abstract class TimeMasterEventDatabase : RoomDatabase() {
    abstract val timeMasterEventDao: TimeMasterEventDao

    companion object {

        @Volatile
        private var INSTANCE: TimeMasterEventDatabase? = null

        fun getInstance(context: Context): TimeMasterEventDatabase {

            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TimeMasterEventDatabase::class.java,
                        "sleep_history_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
