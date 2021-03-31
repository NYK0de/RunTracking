package com.curso.runtracking.runmap

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curso.runtracking.database.RouteDAO
import com.curso.runtracking.database.RunDAO
import com.curso.runtracking.database.RunRoute
import com.curso.runtracking.database.RunTracker
import com.google.android.gms.maps.model.LatLng
import io.sentry.Sentry
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.math.floor

class RunMapViewModel(
    val runDao: RunDAO,
    val runRouteDao: RouteDAO
) : ViewModel() {

    private var todayRuns = MutableLiveData<RunTracker?>()
    private var todayRunPath = MutableLiveData<List<LatLng>>()

    /**
     * Variable that tells the Fragment to navigate to a specific [RunEvaluationFragment]
     * This is 'private' because we don't want to expose setting this value to the Fragment
     */
    private val _navigateToRunEvaluation = MutableLiveData<RunTracker>()
    private val _navigateToRunTracker = MutableLiveData<Boolean>()


    /**
     * If this is non-null, inmediately navigate to [RunEvaluationFragment]
     * and call [doneNavigatingToGrade]
     */
    val navigateToRunEvaluation: LiveData<RunTracker>
        get() = _navigateToRunEvaluation

    val navigateToRunTracker : LiveData<Boolean>
        get() = _navigateToRunTracker

    private var _runDistance = MutableLiveData<Double>()
    private var _metricUnit  = MutableLiveData<String>()
    private var _distanceWithFormat = MutableLiveData<String>()


    val runDistance : LiveData<Double>
        get() = _runDistance

    val metricUnit : LiveData<String>
        get() = _metricUnit

    val distanceWithFormat : LiveData<String>
        get() = _distanceWithFormat

    private var _runDuration = MutableLiveData<Long>()
    private var _totalSecondsCounter = MutableLiveData<String>()
    private var _totalMinCounter = MutableLiveData<String>()
    private var _totalHourCounter = MutableLiveData<String>()
    private var _isChronometerStarted = MutableLiveData<Boolean>()

    val secondsCounter: LiveData<String>
        get() = _totalSecondsCounter

    val minutesCounter : LiveData<String>
        get() = _totalMinCounter

    val hourCounter : LiveData<String>
        get() = _totalHourCounter

    val isChronometerStarted : LiveData<Boolean>
        get() = _isChronometerStarted

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

    fun doneCancelRun() {
        _navigateToRunTracker.value = null
    }

    fun doneNavigatingToGrade(){
        _navigateToRunEvaluation.value = null
    }



    init {
        initializeToday()
    }
    private fun initializeToday() {
        viewModelScope.launch {
            todayRunPath.value = ArrayList<LatLng>()
            _runDistance.value = 0.00
            _metricUnit.value = "Mts"
            _distanceWithFormat.value = "0.00"
            _totalSecondsCounter.value = "00"
            _totalMinCounter.value = "00"
            _totalHourCounter.value = "00"
            _isChronometerStarted.value = false
        }

    }


    fun setRunPath(coordinates : List<LatLng>){
        todayRunPath.value = coordinates
    }

    fun onStartRun(){
        viewModelScope.launch {
            //runDao.clear()
            //runRouteDao.cleanAll()

            val newToday = RunTracker()
            insert(newToday)
            todayRuns.value = getTodayRunFromDatabase()
            //_navigateToRunMap.value = todayRuns.value
            // call to Fragment with the Map

        }
        _isChronometerStarted.value = true
    }

    private suspend fun insert(today: RunTracker){
        runDao.insert(today)
    }

    private suspend fun getTodayRunFromDatabase():  RunTracker? {
        var todayRun = runDao.getRunToday()
        if (todayRun?.endRunTimeMilli != todayRun?.startRunTimeMilli) {
            todayRun = null
        }
        return todayRun
    }

    fun onRunFinish(){
        viewModelScope.launch {
            // Setting this variable to true will alert the observer and trigger navigation
            val oldTodayRun = todayRuns.value ?: return@launch
            oldTodayRun.endRunTimeMilli = System.currentTimeMillis()
            oldTodayRun.runDistance = _runDistance.value?.toFloat() ?: 0f
            update(oldTodayRun)

            try {
                insertRoute(oldTodayRun.runId, todayRunPath.value!!)
            }
            catch (e: Exception){
                Sentry.captureException(e)
            }
            _isChronometerStarted.value = false
            _navigateToRunEvaluation.value = oldTodayRun

        }
    }
    private suspend fun update(runToday: RunTracker){
        runDao.update(runToday)
    }

    fun onRunCancel(){
        viewModelScope.launch {
            todayRuns.value?.runId?.let { runDao.deleteOne(it) }
        }
        _navigateToRunTracker.value = true
    }

    private suspend fun insertRoute(runId: Long, route: List<LatLng>){
        val coordinates = ArrayList<RunRoute>()
        if (route.isNotEmpty()){
            runRouteDao.deleteRoute(runId)
            for (coord in route){
                val runRoute = RunRoute(runId = runId, coordinateLatitude = coord.latitude, coordinateLongitude = coord.longitude)
                coordinates.add(runRoute)
                runRouteDao.insert(coordinates)
            }
        }
        else{
            val e = Exception("There are no coordinates to save")
            throw e
        }
    }

}