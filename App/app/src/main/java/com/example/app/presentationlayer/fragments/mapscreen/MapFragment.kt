package com.example.app.presentationlayer.fragments.mapscreen

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.app.R
import com.example.app.databinding.FragmentMapBinding
import com.example.app.presentationlayer.MainActivity
import com.example.app.presentationlayer.viewmodels.MapFragmentViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.material.snackbar.Snackbar


/**
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding

    private val viewModel by viewModels<MapFragmentViewModel>()

    lateinit var mainActivity: MainActivity

    private lateinit var mapFragment: SupportMapFragment

    private lateinit var googleMap: GoogleMap

    // A default location to use when location permission is not granted. Moscow, Red Square.
    private val defaultLocation = LatLng(55.753544, 37.621202)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = requireActivity() as MainActivity
        mainActivity.onLocationPermissionGrantedForMapFragment = this::updateGeolocationUI
        mapFragment =
            childFragmentManager.findFragmentById(R.id.MapFragment__FragmentContainerView) as SupportMapFragment

        viewModel.fragment = this

        //viewModel.onUpdatePlaces()
        viewModel.onDrawRoute()

        mapFragment.getMapAsync(this)

        // TODO переделать чтобы можно было свитчиться между фрагментами с сохранением состояния
        binding.MapFragmentImageViewPlacesListButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.MapFragmentEditText.hint = viewModel.giveRadiusString()
        binding.MapFragmentEditText.setOnEditorActionListener { textView, actionId, event ->
            viewModel.updateRadius(textView.text.toString())
            Snackbar.make(
                textView,
                "Установлен радиус ${textView.text} км",
                Snackbar.LENGTH_SHORT
            ).show()

            return@setOnEditorActionListener true
        }

    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map

        mainActivity.requestLocationPermission()

        updateGeolocationUI()

    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private fun updateGeolocationUI() {
        try {
            if (mainActivity.locationPermissionGranted) {
                googleMap.isMyLocationEnabled = true
                googleMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                //googleMap.isMyLocationEnabled = false
                //googleMap.uiSettings.isMyLocationButtonEnabled = false
                //mainActivity.lastKnownLocation = null
            }
            updateDeviceLocationPoint()
        } catch (e: SecurityException) {
            Log.e(LOG_TAG, e.message, e)
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private fun updateDeviceLocationPoint() {
        val onSuccess: (location: Location) -> Unit = {
            /*
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        it.latitude,
                        it.longitude
                    ), DEFAULT_ZOOM.toFloat()
                )
            )
             */
        }

        val onFail: () -> Unit = {
            /*
            googleMap.moveCamera(
                CameraUpdateFactory
                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
            )
             */
            //googleMap.uiSettings.isMyLocationButtonEnabled = false
        }

        mainActivity.updateDeviceLocation(onSuccess, onFail)
    }

    fun onNewLocation(
        latitude: Double,
        longitude: Double,
        locationTitle: String,
    ) {
        val onMapReadyCallback = OnMapReadyCallback { googleMap ->
            /**
             * Manipulates the map once available.
             * This callback is triggered when the map is ready to be used.
             * This is where we can add markers or lines, add listeners or move the camera.
             */
            val location = LatLng(latitude, longitude)
            googleMap.addMarker(MarkerOptions().position(location).title(locationTitle))
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    location,
                    DEFAULT_ZOOM.toFloat()
                )
            )
        }

        mapFragment.getMapAsync(onMapReadyCallback)
    }

    fun onDrawRoute(
        pointsList: List<LatLng>,
    ) {
        val onMapReadyCallback = OnMapReadyCallback { googleMap ->
            /**
             * Manipulates the map once available.
             * This callback is triggered when the map is ready to be used.
             * This is where we can add markers or lines, add listeners or move the camera.
             */

            val locationStart = pointsList[0]
            val locationEnd = pointsList.last()
            googleMap.addMarker(MarkerOptions().position(locationStart))
            googleMap.addMarker(MarkerOptions().position(locationEnd))
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(locationStart, DEFAULT_ZOOM.toFloat())
            )

            //val pointsList = PolyUtil.decode("ipkcFjgchVd@@@cF]@@oCK?")
            //Log.d("qwerty123", "$pointsList")

            val routePolyline = googleMap.addPolyline(
                PolylineOptions()
                    .clickable(true)
                    .addAll(pointsList)

            )
            routePolyline.tag = "routePolylineWithArrow"
            stylePolyline(routePolyline)
        }

        mapFragment.getMapAsync(onMapReadyCallback)
    }

    private fun stylePolyline(polyline: Polyline) {
        // Get the data object stored with the polyline.
        val type = polyline.tag?.toString() ?: ""
        when (type) {
            "routePolylineWithArrow" -> {
                polyline.startCap = CustomCap(
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow), 15f)
            }
            else -> polyline.startCap = RoundCap()
        }
        polyline.endCap = RoundCap()
        polyline.width = 12.0F
        polyline.color = ContextCompat.getColor(requireContext(), R.color.route_polyline_color)
        polyline.jointType = JointType.ROUND
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    companion object {

        private const val DEFAULT_ZOOM = 15

        private const val LOG_TAG = "MapFragment"

        @JvmStatic
        fun newInstance(
            //param1: String,
            //param2: String,
        ) = MapFragment().apply {
            arguments = Bundle().apply {
                //putString(ARG_PARAM1, param1)
                //putString(ARG_PARAM2, param2)
            }
        }
    }
}
