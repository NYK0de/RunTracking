package com.curso.runtracking.rundetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.curso.runtracking.database.RouteDAO
import com.curso.runtracking.database.RunDAO
import com.curso.runtracking.database.RunRoute

class RunDetailViewModelFactory(
    private val runKey: Long,
    private val runDaoDataSource: RunDAO,
    private val runRouteDataSource: RouteDAO
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RunDetailViewModel::class.java)) {
            return RunDetailViewModel(runKey, runDaoDataSource, runRouteDataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
