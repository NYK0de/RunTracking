package com.curso.runtracking.rundetail

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
import com.curso.runtracking.databinding.FragmentRunDetailBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class RunDetailFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentRunDetailBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_run_detail, container, false
        )

        val application = requireNotNull(this.activity).application
        val arguments = RunDetailFragmentArgs.fromBundle(arguments!!)

        // Create an instance of the ViewModel Factory.
        val dataSource = RunDatabase.getInstance(application).runDatabaseDao
        val viewModelFactory = RunDetailViewModelFactory(arguments.runKey, dataSource)

        // Get a reference to the ViewModel associated with this fragment.
        val runDetailViewModel = ViewModelProvider( this, viewModelFactory).get(RunDetailViewModel::class.java)

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.runDetailViewModel = runDetailViewModel

        binding.lifecycleOwner = this

        // Add an Observer to the state variable for Navigating when a Quality icon is tapped.
        runDetailViewModel.navigateToRunTracker.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                this.findNavController().navigate(
                    RunDetailFragmentDirections.actionRunDetailFragmentToRunTrackerFragment(arguments.runKey)
                )
                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                runDetailViewModel.doneNavigating()
            }
        })

        //runDetailViewModel.getRun().value.
        mapView = binding.myRunTrackingMap
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //Toast.makeText(activity, "On Map Ready", Toast.LENGTH_LONG).show()

        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(
            MarkerOptions()
            .position(sydney)
            .title("Marker in Sydney"))
        //mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}