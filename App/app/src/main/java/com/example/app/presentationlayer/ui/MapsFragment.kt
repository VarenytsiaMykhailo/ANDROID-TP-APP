package com.example.app.presentationlayer.ui

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.app.R
import com.example.app.datalayer.models.Location
import com.example.app.presentationlayer.viewmodels.LocationRvViewModel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsFragment : Fragment() {
    private val viewModel by viewModels<LocationRvViewModel>()
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }


    private fun onNewLocation(
        latitude: Double,
        longitude: Double,
        locationTitle: String = "Location is here"
    ): OnMapReadyCallback =
        OnMapReadyCallback { googleMap ->
            val location = LatLng(latitude, longitude)
            googleMap.addMarker(MarkerOptions().position(location).title(locationTitle))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            val list = withContext(Dispatchers.IO) { viewModel.getFriends() }
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            for (i in list.indices) {
                val lat = list[i].lat.toDouble()
                val lng = list[i].lng.toDouble()
                val name = list[i].name
                mapFragment?.getMapAsync(onNewLocation(lat, lng, name))
            }
        }
    }

    
}
