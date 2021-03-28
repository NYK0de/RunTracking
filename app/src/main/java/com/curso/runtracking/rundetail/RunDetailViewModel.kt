package com.curso.runtracking.rundetail

import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.*
import com.curso.runtracking.database.RouteDAO
import com.curso.runtracking.database.RunDAO
import com.curso.runtracking.database.RunRoute
import com.curso.runtracking.database.RunTracker
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class RunDetailViewModel(
    private val runtKey: Long = 0L,
    runDao: RunDAO,
    runRouteDao: RouteDAO
) : ViewModel() {

    /**
     * Hold a reference to RunDatabase via its runDao.
     */
    val runDataSource = runDao
    val runRouteDataSource = runRouteDao

    var selectedRunPath = MutableLiveData<List<RunRoute>>()


    /**
     */

    private val run = MediatorLiveData<RunTracker>()
    private val runRoute = MediatorLiveData<List<RunRoute>>()

    fun getRun() = run

    init {
        run.addSource(runDataSource.getRunWithId(runtKey), run::setValue)

        viewModelScope.launch {
            selectedRunPath.value = runRouteDataSource.get(runtKey)
        }
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