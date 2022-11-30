package com.unsyiah.timemaster.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TimeMasterEventDao {

    @Insert
    suspend fun insert(event: TimeMasterEvent)

    @Update
    suspend fun update(event: TimeMasterEvent)

    @Query("SELECT * from time_clock_event_table WHERE id = :key")
    suspend fun get(key: Long): TimeMasterEvent?

    @Delete
    suspend fun delete(event: TimeMasterEvent)

    @Query("DELETE from time_clock_event_table")
    suspend fun clear()

    @Query("SELECT * from time_clock_event_table ORDER BY id DESC LIMIT 1")
    suspend fun getCurrentEvent(): TimeMasterEvent?

    @Query("SELECT * FROM time_clock_event_table ORDER BY id DESC")
    fun getAllEvents(): LiveData<List<TimeMasterEvent>>
}