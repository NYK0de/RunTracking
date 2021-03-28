package com.curso.runtracking.runmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.curso.runtracking.database.RouteDAO
import com.curso.runtracking.database.RunDAO
import java.lang.IllegalArgumentException

class RunMapViewModelFactory(
    private val runEvaluationKey: Long,
    private val runDataSource: RunDAO,
    private val runRouteDataSource: RouteDAO
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RunMapViewModel::class.java)){
            return RunMapViewModel(runEvaluationKey, runDataSource, runRouteDataSource) as T
        }
        throw IllegalArgumentException("UnknowRunMapViewModelClass")
    }

}