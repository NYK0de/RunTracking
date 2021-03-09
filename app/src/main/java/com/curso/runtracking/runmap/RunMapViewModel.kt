package com.curso.runtracking.runmap

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curso.runtracking.database.RunDAO
import com.curso.runtracking.database.RunTracker
import kotlinx.coroutines.launch

class RunMapViewModel(private val runTrackingKey: Long = 0L, val database: RunDAO) : ViewModel() {

    private var todayRuns = MutableLiveData<RunTracker?>()
    //var runDistance = ObservableField(0.0)


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

    fun setDistance(distance: Double){
        _runDistance.value = distance
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
        }
    }

    fun onRunFinish(){
        viewModelScope.launch {
            // Setting this variable to true will alert the observer and trigger navigation
            val oldTodayRun = todayRuns.value ?: return@launch
            oldTodayRun.endRunTimeMilli = System.currentTimeMillis()
            oldTodayRun.runDistance = _runDistance.value?.toFloat() ?: 0f
            update(oldTodayRun)

            _navigateToRunEvaluation.value = oldTodayRun

        }
    }
    private suspend fun update(runToday: RunTracker){
        database.update(runToday)
    }

}