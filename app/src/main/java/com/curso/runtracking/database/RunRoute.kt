package com.curso.runtracking.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_run_tracking_route_table")
data class RunRoute (

    @PrimaryKey(autoGenerate = true)
    var runRouteId: Long = 0L,

    @ColumnInfo(name="run_id")
    var runId: Long = 0L,

    @ColumnInfo(name = "route_point_latitude")
    var coordinateLatitude: Double,

    @ColumnInfo(name = "route_point_longitude")
    var coordinateLongitude: Double,


)