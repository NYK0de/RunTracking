package com.curso.runtracking.rundetail

import android.os.Bundle
import android.util.Log
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
import com.curso.runtracking.database.RunRoute
import com.curso.runtracking.databinding.FragmentRunDetailBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*


class RunDetailFragment : Fragment(), OnMapReadyCallback {

    private var isMapReady = false

    private val COLOR_BLACK_ARGB = -0x10a75ad
    private val POLYLINE_STROKE_WIDTH_PX = 8

    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var runDetailViewModel: RunDetailViewModel
    private val coordinates: ArrayList<LatLng> = ArrayList()


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
        val runDataSource = RunDatabase.getInstance(application).runDatabaseDao
        val runRouteDataSource = RunDatabase.getInstance(application).runRouteDAO

        val viewModelFactory = RunDetailViewModelFactory(arguments.runKey, runDataSource, runRouteDataSource)

        // Get a reference to the ViewModel associated with this fragment.
        runDetailViewModel = ViewModelProvider( this, viewModelFactory).get(RunDetailViewModel::class.java)

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

        runDetailViewModel.selectedRunPath.observe(viewLifecycleOwner, Observer {
            if (it != null){
                if (it.isNotEmpty()) {
                    coordinates.clear()
                    for (eachPoint in it){
                        coordinates.add(LatLng(eachPoint.coordinateLatitude, eachPoint.coordinateLongitude))
                    }
                    drawRouteOnMap()
                }
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

        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        isMapReady = true
        drawRouteOnMap()
    }

    private fun drawRouteOnMap(){
        if (!isMapReady) return

        var polyline: Polyline?
        if (coordinates.isNotEmpty()){
            polyline = mMap.addPolyline(
                PolylineOptions().clickable(true).addAll(coordinates)
            )
            polyline?.endCap = RoundCap()
            polyline?.width = POLYLINE_STROKE_WIDTH_PX.toFloat()
            polyline?.color = COLOR_BLACK_ARGB
            polyline?.jointType = JointType.ROUND

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates[0], 17f))
            // Add a marker in Start run point and move the camera

            mMap.addMarker(
                MarkerOptions()
                    .position(coordinates[0])
                    .title("Start")
            )
        }
        else{
            Log.v("RunRoute", "Route is null: ${coordinates.toString()}")
        }
    }
}