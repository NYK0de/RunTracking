package com.curso.runtracking.runtracking

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.curso.runtracking.database.RunDAO
import com.curso.runtracking.database.RunTracker
import com.curso.runtracking.formatRuns
import kotlinx.coroutines.*

class RunTrackerViewModel(val database: RunDAO,
                          application: Application) : AndroidViewModel(application)
{

    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    // Define the Scope to use with coroutines (in firts step of coroutines)
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //define a variable today
    private var runToday = MutableLiveData<RunTracker?>()

    private val runsOfAllDays = database.getAllRuns()
    /**
     * Converted nights to Spanned for displaying.
     */
    val nightsString = Transformations.map(runsOfAllDays) { runsOfAllDays ->
        formatRuns(runsOfAllDays, application.resources)
    }

    init {
        initializeToday()
    }

    private fun initializeToday() {
        uiScope.launch {
            runToday.value = getTodayRunFromDatabase()
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
        uiScope.launch {
            val newToday = RunTracker()
            insert(newToday)
            runToday.value = getTodayRunFromDatabase()
        }
    }

    private suspend fun insert(today: RunTracker){
        withContext(Dispatchers.IO){
            database.insert(today)
        }
    }

    fun onStopTracking(){
        uiScope.launch {
            val oldTodayRun = runToday.value ?: return@launch
            oldTodayRun.endRunTimeMilli = System.currentTimeMillis()
            update(oldTodayRun)
        }
    }
    private suspend fun update(runToday: RunTracker) {
        withContext(Dispatchers.IO){
            database.update(runToday)
        }

    }


    fun onClear(){
        uiScope.launch {
            clear()
            runToday.value = null
        }
    }
    private suspend fun clear() {
        withContext(Dispatchers.IO){
            database.clear()
        }
    }



}