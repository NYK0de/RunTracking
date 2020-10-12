package com.curso.runtracking.runtracking

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.curso.runtracking.database.RunDAO

class RunTrackerViewModelFactory(private val datasource: RunDAO,
                                 private val application: Application) : ViewModelProvider.Factory
{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if(modelClass.isAssignableFrom(RunTrackerViewModel::class.java)){
            return RunTrackerViewModel(datasource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}