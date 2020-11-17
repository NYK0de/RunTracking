package com.curso.runtracking.runtracking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.curso.runtracking.R
import com.curso.runtracking.database.RunDatabase
import com.curso.runtracking.databinding.FragmentRunTrackerBinding
import com.google.android.material.snackbar.Snackbar


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

        // getting an instance of the datasource (Dao)
        val datasource = RunDatabase.getInstance(application).runDatabaseDao

        // Creating a view model factory
        val viewModelFactory = RunTrackerViewModelFactory(datasource, application)

        // Creating an instance of the viewModel of this fragment
        val runTrackerviewModel = ViewModelProvider(this, viewModelFactory).get(RunTrackerViewModel::class.java)

        // giving a viewModel to the layout for binding
        binding.runTrackerViewModel = runTrackerviewModel
        //giving a lifeCycle Owner
        binding.lifecycleOwner = this

        val runTrackerAdapter = RunTrackerAdapter()

        binding.runList.adapter = runTrackerAdapter

        // Adding an observer on the state variable for navigating when STOP BUTTON is pressed
        runTrackerviewModel.navigateToRunEvaluation.observe(viewLifecycleOwner, Observer {run ->
            run?.let {
                this.findNavController().navigate(
                    RunTrackerFragmentDirections.actionRunTrackerFragmentToRunEvaluationFragment(run.runId))

                runTrackerviewModel.doneNavigating()
            }
        })

        runTrackerviewModel.showSnackBarEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                Snackbar.make(
                    activity!!.findViewById(android.R.id.content),
                    getString(R.string.cleared_message),
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()
                runTrackerviewModel.doneShowingSnackbar()
            }
        })

        runTrackerviewModel.runsOfAllDays.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it != null) {
                    runTrackerAdapter.submitList(it)
                }
            }
        })

        // returning the root view of the layout
        return binding.root
    }


}