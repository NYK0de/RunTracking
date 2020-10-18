/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.curso.runtracking.runevaluation

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curso.runtracking.database.RunDAO
import kotlinx.coroutines.launch


class RunEvaluationViewModel(
    private val runTrackingKey: Long = 0L,
    val database: RunDAO
) : ViewModel(){

    var runDistance = ObservableField("")

    /**
     * Variable that tells the fragment whether it should navigate to [RunTrackerFragment]
     * This is 'private' because we don't want to expose the ability to set [MutableLiveData] to
     * the [Fragment]
     */
    private val _navigateToRunTracker = MutableLiveData<Boolean?>()


    /**
     * When - true - inmediatly navigate back to the [RunTrackerFragment]
     */
    val navigateToRunTracking: LiveData<Boolean?>
        get() = _navigateToRunTracker



    /**
     * Call this immediately after navigating to [RunTrackerFragment]
     */
    fun doneNavigating() {
        _navigateToRunTracker.value = null
    }

    fun onSetRunQuality(runQuality: Int){
        viewModelScope.launch {
            val today = database.get(runTrackingKey) ?: return@launch

            today.runEvaluation = runQuality
            database.update(today)


        }
    }

    fun onSaveRunResult(){
        viewModelScope.launch {
            // Setting this variable to true will alert the observer and trigger navigation
            val today = database.get(runTrackingKey) ?: return@launch

            today.runDistance = runDistance.get()?.toFloat() ?: 0f
            database.update(today)

            _navigateToRunTracker.value = true
        }
    }
}
