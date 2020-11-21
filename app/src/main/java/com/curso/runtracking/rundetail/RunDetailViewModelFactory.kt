package com.curso.runtracking.rundetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.curso.runtracking.database.RunDAO

class RunDetailViewModelFactory(
    private val runKey: Long,
    private val dataSource: RunDAO) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RunDetailViewModel::class.java)) {
            return RunDetailViewModel(runKey, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
