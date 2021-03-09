package com.curso.runtracking.runmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.curso.runtracking.database.RunDAO
import java.lang.IllegalArgumentException

class RunMapViewModelFactory(
    private val runEvaluationKey: Long,
    private val dataSource: RunDAO
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RunMapViewModel::class.java)){
            return RunMapViewModel(runEvaluationKey, dataSource) as T
        }
        throw IllegalArgumentException("UnknowRunMapViewModelClass")
    }

}