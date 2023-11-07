package com.example.app.presentationlayer.fragments.mapscreen

import android.location.Location
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.app.R
import com.example.app.databinding.FragmentMapBinding
import com.example.app.datalayer.models.NearbyPlace
import com.example.app.presentationlayer.MainActivity
import com.example.app.presentationlayer.viewmodels.MapFragmentViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


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

        viewModel.onUpdatePlaces()

        mapFragment.getMapAsync(this)

        // TODO переделать чтобы можно было свитчиться между фрагментами с сохранением состояния
        binding.MapFragmentImageViewPlacesListButton.setOnClickListener {
            parentFragmentManager.popBackStack()
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
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        it.latitude,
                        it.longitude
                    ), DEFAULT_ZOOM.toFloat()
                )
            )
        }

        val onFail: () -> Unit = {
            googleMap.moveCamera(
                CameraUpdateFactory
                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
            )
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
             * In this case, we just add a marker near Sydney, Australia.
             * If Google Play services is not installed on the device, the user will be prompted to
             * install it inside the SupportMapFragment. This method will only be triggered once the
             * user has installed Google Play services and returned to the app.
             */
            val location = LatLng(latitude, longitude)
            googleMap.addMarker(MarkerOptions().position(location).title(locationTitle))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        }

        mapFragment.getMapAsync(onMapReadyCallback)
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
