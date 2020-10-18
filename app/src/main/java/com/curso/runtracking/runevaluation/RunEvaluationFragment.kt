package com.curso.runtracking.runevaluation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.curso.runtracking.R
import com.curso.runtracking.database.RunDatabase
import com.curso.runtracking.databinding.FragmentRunEvaluationBinding

/**
 * Fragment that displays a list of clickable icons,
 * each representing a sleep quality rating.
 * Once the user taps an icon, the quality is set in the current sleepNight
 * and the database is updated.
 */
class RunEvaluationFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentRunEvaluationBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_run_evaluation, container, false)

        val application = requireNotNull(this.activity).application

        // getting the argument passed by navigation
        val arguments = RunEvaluationFragmentArgs.fromBundle(arguments!!)
        // Getting the dataSource Instance
        val dataSource = RunDatabase.getInstance(application).runDatabaseDao
        // Getting the "runTrackingKey" argument passed as argument in navigation action
        val viewModelFactory = RunEvaluationViewModelFactory(arguments.runTrackingKey, dataSource)
        // Getting an Instance of the viewModel for this Frangment
        val evaluationViewModel = ViewModelProvider(this, viewModelFactory)
            .get(RunEvaluationViewModel::class.java)
        // Setting the variable to the Layout corresponding to the viewModel to will be used
        binding.runEvaluationViewModel = evaluationViewModel

        // add an observer
        evaluationViewModel.navigateToRunTracking.observe(viewLifecycleOwner, Observer {
            if (it == true){
                this.findNavController().navigate(
                    RunEvaluationFragmentDirections.actionRunEvaluationFragmentToRunTrackerFragment())
                evaluationViewModel.doneNavigating()
            }
        })


        return binding.root
    }
}
