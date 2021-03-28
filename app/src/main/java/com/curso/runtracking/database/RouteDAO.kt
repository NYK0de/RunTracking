package com.curso.runtracking.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RouteDAO {

    /**
     * get al db records
     */
    @Query("SELECT * FROM daily_run_tracking_route_table")
    suspend fun getAll(): List<RunRoute>?

    /**
     * Insert a route where a user ran
     */
    @Insert
    suspend fun insert(route: List<RunRoute>)

    /**
     * getting a row by runId
     */
    @Query("SELECT * FROM daily_run_tracking_route_table WHERE run_id = :runId")
    suspend fun get(runId: Long): List<RunRoute>?

    /**
     * deleting a specific route
     */
    @Query("DELETE FROM daily_run_tracking_route_table WHERE run_id = :runId")
    suspend fun deleteRoute(runId: Long)

    @Query("DELETE FROM daily_run_tracking_route_table")
    suspend fun cleanAll()

    /**
     * getting a row by runId
     */
    @Query("SELECT * FROM daily_run_tracking_route_table WHERE run_id = :runId")
    fun getRouteWithRunId(runId: Long): LiveData<List<RunRoute>>

}