package com.curso.runtracking.runtracking

import android.app.Application
import androidx.lifecycle.*
import com.curso.runtracking.database.RunDAO
import com.curso.runtracking.database.RunTracker
import com.curso.runtracking.formatRuns
import kotlinx.coroutines.*

class RunTrackerViewModel(val database: RunDAO,
                          application: Application) : AndroidViewModel(application)
{

    // ------ declaring variables to manage navigation -------------------------
    private var todayRuns = MutableLiveData<RunTracker?>()

    val runsOfAllDays = database.getAllRuns()
    /**
     * Converted dayRunning to Spanned for displaying.
     */
    val allDaysString = Transformations.map(runsOfAllDays) { runsOfAllDays ->
        formatRuns(runsOfAllDays, application.resources)
    }

    val startButtonVisible = Transformations.map(todayRuns) {
        null == it
    }
    val stopButtonVisible = Transformations.map(todayRuns) {
        null != it
    }
    val clearButtonVisible = Transformations.map(runsOfAllDays) {
        it?.isNotEmpty()
    }
    /**
     * Request a toast by setting this value to true.
     *
     * This is private because we don't want to expose setting this value to the Fragment.
     */
    private var _showSnackbarEvent = MutableLiveData<Boolean>()

    /**
     * If this is true, immediately `show()` a toast and call `doneShowingSnackbar()`.
     */
    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent

    //TODO (04) Using the familiar pattern, create encapsulated showSnackBarEvent variable
    //and doneShowingSnackbar() fuction.

    //TODO (06) In onClear(), set the value of _showOnSnackbarEvent to true.


    /**
     * Variable that tells the Fragment to navigate to a specific [RunEvaluationFragment]
     * This is 'private' because we don't want to expose setting this value to the Fragment
     */
    private val _navigateToRunEvaluation = MutableLiveData<RunTracker>()

    /**
     * Call this immediately after calling `show()` on a toast.
     *
     * It will clear the toast request, so if the user rotates their phone it won't show a duplicate
     * toast.
     */

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }

    /**
     * If this is non-null, inmediately navigate to [RunEvaluationFragment]
     * and call [doneNavigating]
     */
    val navigateToRunEvaluation: LiveData<RunTracker>
        get() = _navigateToRunEvaluation

    /**
     * Call this inmediatly after navigating to [RunEvalationFragment]
     * It will clear the navigation request,
     * so if the user rotates their phone it won't navigate twice
     */
    fun doneNavigating(){
        _navigateToRunEvaluation.value = null
    }
    // ------ End of declaring variables for navigation components -------------

    init {
        initializeToday()
    }
    private fun initializeToday() {
        viewModelScope.launch {
            todayRuns.value = getTodayRunFromDatabase()
        }
    }

    private suspend fun getTodayRunFromDatabase():  RunTracker? {

            var todayRun = database.getRunToday()
            if (todayRun?.endRunTimeMilli != todayRun?.startRunTimeMilli) {
                todayRun = null
            }
            return todayRun
    }

    fun onStartTracking(){
        viewModelScope.launch {
            val newToday = RunTracker()
            insert(newToday)
            todayRuns.value = getTodayRunFromDatabase()
        }
    }

    private suspend fun insert(today: RunTracker){
        database.insert(today)
    }


    fun onStopTracking(){
        viewModelScope.launch {
            val oldTodayRun = todayRuns.value ?: return@launch
            oldTodayRun.endRunTimeMilli = System.currentTimeMillis()
            update(oldTodayRun)
            _navigateToRunEvaluation.value = oldTodayRun
        }
    }
    private suspend fun update(runToday: RunTracker){
        database.update(runToday)
    }


    fun onClear(){
        viewModelScope.launch {
            clear()
            todayRuns.value = null
            _showSnackbarEvent.value = true
        }
    }
    private suspend fun clear(){
        database.clear()
    }


}