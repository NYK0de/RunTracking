package com.curso.runtracking.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RunDAO {

    /**
     * To insert a row
     */
    @Insert
    suspend fun insert(run: RunTracker)

    /**
     * To update a row
     */
    @Update
    suspend fun update(run: RunTracker)

    /**
     * getting a row by runId
     */
    @Query("SELECT * FROM daily_run_tracking_table WHERE runId = :key")
    suspend fun get(key: Long): RunTracker?

    /**
     * Clear all the data in the table
     */
    @Query("DELETE FROM daily_run_tracking_table")
    suspend fun clear()

    /**
     * all the rows sorted by id DESC
     */
    @Query("SELECT * FROM daily_run_tracking_table ORDER BY runId DESC")
    fun getAllRuns(): LiveData<List<RunTracker>>

    /**
     * to get an row
     */
    @Query("SELECT * FROM daily_run_tracking_table ORDER BY runId DESC LIMIT 1")
    suspend fun getRunToday(): RunTracker?
}