package com.curso.runtracking.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_run_tracking_table")
data class RunTracker (

    @PrimaryKey(autoGenerate = true)
    var runId: Long = 0L,

    @ColumnInfo(name = "start_time_running")
    val startRunTimeMilli: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "end_time_running")
    var endRunTimeMilli: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "run_distance")
    var runDistance: Float = 0f,

    @ColumnInfo(name = "run_evaluation")
    var runEvaluation: Int = -1
)