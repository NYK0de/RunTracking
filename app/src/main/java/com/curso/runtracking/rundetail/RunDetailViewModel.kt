package com.curso.runtracking.rundetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.curso.runtracking.database.RunDAO
import com.curso.runtracking.database.RunTracker

class RunDetailViewModel (private val runtKey: Long = 0L, dataSource: RunDAO) : ViewModel() {

    /**
     * Hold a reference to SleepDatabase via its SleepDatabaseDao.
     */
    val database = dataSource


    /**
     */

    private val run = MediatorLiveData<RunTracker>()

    fun getRun() = run

    init {
        run.addSource(database.getRunWithId(runtKey), run::setValue)
    }

    /**
     * Variable that tells the fragment whether it should navigate to [runTrackerFragment].
     *
     * This is `private` because we don't want to expose the ability to set [MutableLiveData] to
     * the [Fragment]
     */
    private val _navigateToRunTracker = MutableLiveData<Boolean?>()

    /**
     * When true immediately navigate back to the [runTrackerFragment]
     */
    val navigateToRunTracker: LiveData<Boolean?>
        get() = _navigateToRunTracker

    /**
     *
     */


    /**
     * Call this immediately after navigating to [RunTrackerFragment]
     */
    fun doneNavigating() {
        _navigateToRunTracker.value = null
    }

    fun onClose() {
        _navigateToRunTracker.value = true
    }

}