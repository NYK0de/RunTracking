package com.curso.runtracking.runtracking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.curso.runtracking.R
import com.curso.runtracking.database.RunDatabase
import com.curso.runtracking.databinding.FragmentRunTrackerBinding


/**
 * A simple [Fragment] subclass.
 * Use the [RunTrackerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RunTrackerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentRunTrackerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_run_tracker, container, false)

        val application = requireNotNull(this.activity).application

        val datasource = RunDatabase.getInstance(application).runDatabaseDao

        val viewModelFactory = RunTrackerViewModelFactory(datasource, application)

        val viewModel = ViewModelProvider(this, viewModelFactory).get(RunTrackerViewModel::class.java)

        binding.runTrackerViewModel = viewModel

        //TODO (01) Update onCreateView() to get an instance of SleepTrackerViewModel
        //using the factory.

        //TODO (02) Update to set this as the lifecycle owner of the binding.

        //TODO (04) Update to assign sleepTrackerViewModel binding variable
        //to the sleepTrackerViewModel.
        binding.lifecycleOwner = this
        return binding.root
    }


}