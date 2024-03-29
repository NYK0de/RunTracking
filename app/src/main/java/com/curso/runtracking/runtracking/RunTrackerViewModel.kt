package com.curso.runtracking.runtracking

import android.app.Application
import androidx.lifecycle.*
import com.curso.runtracking.database.RunDAO
import com.curso.runtracking.database.RunTracker
import kotlinx.coroutines.*

class RunTrackerViewModel(val database: RunDAO,
                          application: Application) : AndroidViewModel(application)
{

    // ------ declaring variables to manage navigation -------------------------
    private var todayRuns = MutableLiveData<RunTracker?>()

    val runsOfAllDays = database.getAllRuns()


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
     * Variable that tells the Fragment to navigate to a specific [RunTrackerMapFragment]
     * This is 'private' because we don't want to expose setting this value to the Fragment
     */
    //private val _navigateToRunMap = MutableLiveData<RunTracker>()
    private val _navigateToRunMapBool = MutableLiveData<Boolean>()

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
     * and call [doneNavigatingToGrade]
     */
    val navigateToRunEvaluation: LiveData<RunTracker>
        get() = _navigateToRunEvaluation

    /**
     * If this is non-null, inmediately navigate to [RunTrackerMapFragment]
     * and call [doneNavigatingToGrade]
     */
    //val navigateToRunMapFragment: LiveData<RunTracker>
    //    get() = _navigateToRunMap
    val navigateToRunMapFragment: LiveData<Boolean>
        get() = _navigateToRunMapBool


    /**
     * Call this inmediatly after navigating to [RunEvalationFragment]
     * It will clear the navigation request,
     * so if the user rotates their phone it won't navigate twice
     */
    fun doneNavigatingToGrade(){
        _navigateToRunEvaluation.value = null
    }

    /**
     * Call this inmediatly after navigating to [RunTrackerMapFragment]
     * It will clear the navigation request,
     * so if the user rotates their phone it won't navigate twice
     */
    fun doneNavigatingToMap(){
        //_navigateToRunMap.value = null
        _navigateToRunMapBool.value = null
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
        _navigateToRunMapBool.value = true
    }

    private suspend fun insert(today: RunTracker){
        database.insert(today)
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

    private val _navigateToRunDetails = MutableLiveData<Long>()

    val navigateToRunDetails
        get() = _navigateToRunDetails



    fun onRunClicked(id: Long){
        _navigateToRunDetails.value = id
    }
    fun onDetailsNavigated(){
        _navigateToRunDetails.value = null
    }


}