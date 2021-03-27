package com.curso.runtracking.runmap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curso.runtracking.database.RunDAO
import com.curso.runtracking.database.RunTracker
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import kotlin.math.floor

class RunMapViewModel(
    private val runTrackingKey: Long = 0L,
    val database: RunDAO
) : ViewModel() {

    private var todayRuns = MutableLiveData<RunTracker?>()
    private var todayRunPath = MutableLiveData<List<LatLng>>()

    /**
     * Variable that tells the Fragment to navigate to a specific [RunEvaluationFragment]
     * This is 'private' because we don't want to expose setting this value to the Fragment
     */
    private val _navigateToRunEvaluation = MutableLiveData<RunTracker>()


    /**
     * If this is non-null, inmediately navigate to [RunEvaluationFragment]
     * and call [doneNavigatingToGrade]
     */
    val navigateToRunEvaluation: LiveData<RunTracker>
        get() = _navigateToRunEvaluation


    var _runDistance = MutableLiveData<Double>()
    var _metricUnit  = MutableLiveData<String>()
    var _distanceWithFormat = MutableLiveData<String>()


    val runDistance : LiveData<Double>
        get() = _runDistance

    val metricUnit : LiveData<String>
        get() = _metricUnit

    val distanceWithFormat : LiveData<String>
        get() = _distanceWithFormat

    var _runDuration = MutableLiveData<Long>()
    var _totalSecondsCounter = MutableLiveData<String>()
    var _totalMinCounter = MutableLiveData<String>()
    var _totalHourCounter = MutableLiveData<String>()

    val secondsCounter: LiveData<String>
        get() = _totalSecondsCounter

    val minutesCounter : LiveData<String>
        get() = _totalMinCounter

    val hourCounter : LiveData<String>
        get() = _totalHourCounter


    fun setDistance(distance: Double){
        _runDistance.value = distance
        distanceFormat(distance)
    }

    fun setSecondsCounter(miliSeconds: Long){
        setRunDuration(miliSeconds)
        val seconds = miliSeconds/1000
        val restSeconds = seconds % 60
        val minutes = floor((seconds / 60).toDouble()).toInt()
        val hours = floor((seconds / (60*60)).toDouble()).toInt()

        _totalSecondsCounter.value = if(restSeconds.toInt() > 9) "${restSeconds.toInt()}" else  "0${restSeconds.toInt()}"
        _totalMinCounter.value = if(minutes > 9) "$minutes" else "0$minutes"
        _totalHourCounter.value = if(hours > 9) "$hours" else "0$hours"
    }

    fun setRunDuration(miliSeconds: Long){
        _runDuration.value = miliSeconds
    }

    fun distanceFormat(dist: Double){
        var newDist = dist
        if (dist >= 100){
            newDist = dist/1000
            _metricUnit.value = "Kms"
        }
        _distanceWithFormat.value = String.format("%.${2}f", newDist)

    }
    /**
     * Call this immediately after navigating to [RunTrackerFragment]
     */
    fun doneNavigating() {
        _navigateToRunEvaluation.value = null
    }

    fun doneNavigatingToGrade(){
        _navigateToRunEvaluation.value = null
    }



    init {
        initializeToday()
    }
    private fun initializeToday() {
        viewModelScope.launch {
            todayRuns.value = database.get(runTrackingKey)
            todayRunPath.value = ArrayList<LatLng>()
            _runDistance.value = 0.07
            _metricUnit.value = "Mts"
            _distanceWithFormat.value = "0.00"
            _totalSecondsCounter.value = "00"
            _totalMinCounter.value = "00"
            _totalHourCounter.value = "00"
        }

    }


    fun setRunPath(coordinates : List<LatLng>){
        todayRunPath.value = coordinates
    }

    fun onRunFinish(){
        viewModelScope.launch {
            // Setting this variable to true will alert the observer and trigger navigation
            val oldTodayRun = todayRuns.value ?: return@launch
            oldTodayRun.endRunTimeMilli = System.currentTimeMillis()
            oldTodayRun.runDistance = _runDistance.value?.toFloat() ?: 0f
            update(oldTodayRun)

            // Todo: Verify if loctions of polilyne exists

            _navigateToRunEvaluation.value = oldTodayRun

        }

    }
    private suspend fun update(runToday: RunTracker){
        database.update(runToday)
    }

}