package com.example.app.presentationlayer.fragments.placepickermapscreen

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.example.app.R
import com.example.app.databinding.FragmentPlacePickerMapBinding
import com.example.app.presentationlayer.MainActivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.Locale


/**
 * Use the [PlacePickerMapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlacePickerMapFragment :
    Fragment(),
    OnMapReadyCallback,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraMoveStartedListener,
    GoogleMap.OnCameraIdleListener {

    private lateinit var binding: FragmentPlacePickerMapBinding

    private lateinit var mainActivity: MainActivity

    private lateinit var mapFragment: SupportMapFragment

    private lateinit var googleMap: GoogleMap

    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    private lateinit var choosedLocation: LatLng

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlacePickerMapBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = requireActivity() as MainActivity
        mainActivity.onLocationPermissionGrantedForPlacePickerMapFragment = this::updateGeolocationUI
        mapFragment =
            childFragmentManager.findFragmentById(R.id.PlacePickerMapFragment__FragmentContainerView) as SupportMapFragment

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.LAT_LNG, Place.Field.NAME))
        //autocompleteFragment.view?.findViewById<EditText>(R.id.autocomplete_fragment)?.setTextColor(resources.getColor(R.color.white,null))
        //autocompleteFragment.setCountries("RU")

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i(LOG_TAG, "Got palce from autocomplete: ${place.latLng}, ${place.name}")
                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(place.latLng!!, DEFAULT_ZOOM.toFloat())
                )
            }

            override fun onError(status: Status) {
                // TODO show snackbar
                Log.e(LOG_TAG, "An error occurred: $status")
            }
        })

        binding.PlacePickerMapFragmentButtonSelect.setOnClickListener {
            mainActivity.usersLastChosenLocation = choosedLocation
            parentFragmentManager.popBackStack()
        }

        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map

        googleMap.setOnCameraMoveListener(this)
        googleMap.setOnCameraMoveStartedListener(this)
        googleMap.setOnCameraIdleListener(this)

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
                configureUserLocationButtonPosition()
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
                CameraUpdateFactory.newLatLngZoom(
                    mainActivity.usersLastChosenLocation, DEFAULT_ZOOM.toFloat()
                )
            )
        }

        mainActivity.updateDeviceLocation(onSuccess, onFail)
    }

    override fun onCameraMove() {
    }

    override fun onCameraMoveStarted(p0: Int) {
    }

    override fun onCameraIdle() {
        val cameraPosition = googleMap.cameraPosition
        val lat = cameraPosition.target.latitude
        val lng = cameraPosition.target.longitude
        choosedLocation = LatLng(lat, lng)
        updateAddressInAutocomplete(lat, lng)
    }

    private fun updateAddressInAutocomplete(lat: Double, lng: Double) {
        val geocoder = Geocoder(this.requireContext(), Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(lat, lng, 1)
            val obj: Address = addresses!![0]
            val add: String = obj.getAddressLine(0)
            autocompleteFragment.setText(add)
        } catch (e: Exception) {
            // TODO Переделать
            e.printStackTrace()
            //Toast.makeText(this.requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun configureUserLocationButtonPosition() {
        val locationButton = mapFragment.view?.findViewWithTag<View>("GoogleMapMyLocationButton")
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
            addRule(RelativeLayout.CENTER_VERTICAL)
            marginEnd = 18
        }
        locationButton?.layoutParams = layoutParams
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SmallMapFragment.
     */
    companion object {

        private const val LOG_TAG = "PlacePickerMapFragment"

        private const val DEFAULT_ZOOM = 12

        @JvmStatic
        fun newInstance(
            //param1: String,
            //param2: String,
        ) = PlacePickerMapFragment().apply {
            arguments = Bundle().apply {
                //putString(ARG_PARAM1, param1)
                //putString(ARG_PARAM2, param2)
            }
        }
    }
}