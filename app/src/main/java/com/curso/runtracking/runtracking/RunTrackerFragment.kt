package com.curso.runtracking.runtracking

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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

    private var _binding: FragmentRunTrackerBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Get a reference to the binding object and inflate the fragment views.
        _binding = FragmentRunTrackerBinding.inflate(inflater, container, false)

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

        val runTrackerAdapter = RunTrackerAdapter(RunTrackListener {
            runId -> runTrackerviewModel.onRunClicked(runId)
        })

        //val manager = GridLayoutManager(activity, 2)
        val manager = LinearLayoutManager(activity)
        binding.runList.layoutManager = manager

        binding.runList.adapter = runTrackerAdapter

        runTrackerviewModel.navigateToRunDetails.observe(viewLifecycleOwner, Observer { run ->
            run?.let {
                this.findNavController().navigate(RunTrackerFragmentDirections.actionRunTrackerFragmentToRunDetailFragment(run))
                runTrackerviewModel.onDetailsNavigated()
            }
        })

        runTrackerviewModel.navigateToRunMapFragment.observe(viewLifecycleOwner, Observer {run ->
            if (run != null){
                Log.v("NAVFRAGMENT", "Navigating to Map Fragment")
                this.findNavController().navigate(
                    RunTrackerFragmentDirections.actionRunTrackerFragmentToRunMapFragment(0L))
                runTrackerviewModel.doneNavigatingToMap()
            }
        })

        runTrackerviewModel.navigateToRunMapFragment.observe(viewLifecycleOwner, Observer { run ->

            run?.let {

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
                runTrackerAdapter.submitList(it)
            }
        })

        // returning the root view of the layout
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear -> {
                // navigate to settings screen
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}