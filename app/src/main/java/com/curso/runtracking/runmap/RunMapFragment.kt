package com.curso.runtracking.runmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.Chronometer.OnChronometerTickListener
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.curso.runtracking.R
import com.curso.runtracking.database.RunDatabase
import com.curso.runtracking.databinding.FragmentRunMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import io.sentry.Sentry
import kotlinx.android.synthetic.main.fragment_run_map.*


class RunMapFragment : Fragment(), LocationListener {

    private var _binding: FragmentRunMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var runViewModel: RunMapViewModel
    private lateinit var locationManager: LocationManager

    private val COLOR_BLACK_ARGB = -0x10a75ad
    private val POLYLINE_STROKE_WIDTH_PX = 8

    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var map: GoogleMap
    private var isMapReady = false
    private var locations = ArrayList<LatLng>()
    private var polyline : Polyline? = null
    private var distance : Double = 0.0;

    lateinit var chronometer: Chronometer


    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        isMapReady = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRunMapBinding.inflate(inflater, container, false)
        val viewRoot = binding.root
        val application = requireNotNull(this.activity).application

        locationManager =
            activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Getting the dataSource Instance
        val runDataSource = RunDatabase.getInstance(application).runDatabaseDao
        val routeDataSource = RunDatabase.getInstance(application).runRouteDAO
        // get view model instance through a ViewModelFactory
        val viewModelFactory = RunMapViewModelFactory(runDataSource, routeDataSource)

        runViewModel = ViewModelProvider(this, viewModelFactory).get(RunMapViewModel::class.java)
        // setting the viewModel to our layout as a variable
        binding.runMapViewModel = runViewModel
        binding.lifecycleOwner = this

        // Show received argument
        runViewModel.navigateToRunEvaluation.observe(viewLifecycleOwner, Observer {run ->
            run?.let {
                this.findNavController().navigate(
                    RunMapFragmentDirections.actionRunMapFragmentToRunEvaluationFragment(run.runId)
                )
                runViewModel.doneNavigating()
            }
        })

        // action_run_map_fragment_to_run_tracker_fragment
        runViewModel.navigateToRunTracker.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate != null){
                this.findNavController().navigate(
                    RunMapFragmentDirections.actionRunMapFragmentToRunTrackerFragment(0L)
                )
                runViewModel.doneCancelRun()
                Log.v("NAVBACK", "Navigating to back Fragment")
            }
        })

        enableMyLocation()
        chronometer = binding.chronometer


        runViewModel.isChronometerStarted.observe(viewLifecycleOwner, Observer { t ->
            if (t){
                startChronometer()
            }
            else{
                chronometer.stop()
            }
        })

        return viewRoot
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(this)
    }

    private fun startChronometer(){
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.onChronometerTickListener =
            OnChronometerTickListener {
                    chronometerChanged -> chronometer = chronometerChanged
                val elapsedMillis = (SystemClock.elapsedRealtime() - chronometer.base)
                if (elapsedMillis > 3600000L){
                    chronometer.format = "0%s"
                }
                else {
                    chronometer.format = "00:%s"
                }
                runViewModel.setSecondsCounter(elapsedMillis)
            }
        chronometer.start()
        finishRun.visibility = View.VISIBLE
        startRun.visibility = View.INVISIBLE
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(),Manifest.permission.ACCESS_FINE_LOCATION)
                                                                        != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)
                                                                        != PackageManager.PERMISSION_GRANTED
        ) {
            // If we don't have permission, we request the required permission to show the functionality
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            // to show our current location in the map (a little blue circle)
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                500,
                2.0f,
                this
            )
            if (isMapReady) {
                map.isMyLocationEnabled = true
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,grantResults: IntArray) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()

            }
        }
    }

    /**
     * @description: Track our location and draw our route, too show the distance we are walked
     */
    @SuppressLint("MissingPermission")
    override fun onLocationChanged(p0: Location) {
        try {
            locations.add(LatLng(p0.latitude, p0.longitude))

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(p0.latitude, p0.longitude), 17f))
            if (isMapReady) {
                map.isMyLocationEnabled = true
            }

            if (locations.size >= 2) {
                // We calculate the distance between the last location and the current location
                // And then we increment the global distance walked/runned from the first location and the last location
                val dist = SphericalUtil.computeDistanceBetween(
                    locations[locations.size-2],
                    locations[locations.size-1]
                )
                distance += dist
                runViewModel.setDistance(distance)

                val strDistance:Double = String.format("%.${2}f", distance).toDouble() // formatting distance String

                polyline?.remove()

                polyline = map.addPolyline(
                    PolylineOptions().clickable(true).addAll(locations)
                )
                polyline?.endCap = RoundCap()
                polyline?.width = POLYLINE_STROKE_WIDTH_PX.toFloat()
                polyline?.color = COLOR_BLACK_ARGB
                polyline?.jointType = JointType.ROUND

                polyline?.points?.let { it1 -> runViewModel.setRunPath(it1) }

                tvStatus?.text = "distancia: $strDistance" // showing distance to the user

            }
        } catch (e: Exception){
            Toast.makeText(requireContext(), "Lo sentimos, ocurrio un problema", Toast.LENGTH_LONG).show()
            // sending exception to Sentry to detect and fix posible bugs
            Sentry.captureException(e)
        }
    }

    /**
     *
     */
    override fun onProviderEnabled(provider: String) {

    }

    /**
     *
     */
    override fun onProviderDisabled(provider: String) {

    }

    /**
     *
     */
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

}