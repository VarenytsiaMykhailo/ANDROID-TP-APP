package com.example.app.presentationlayer.fragments

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import com.example.app.R
import com.example.app.presentationlayer.viewmodels.MapsFragmentViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private val viewModel by viewModels<MapsFragmentViewModel>()

    private lateinit var mapFragment: SupportMapFragment

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFragment = childFragmentManager.findFragmentById(R.id.MapsFragment__FragmentContainerView) as SupportMapFragment
        viewModel.fragment = this

        viewModel.onUpdatePlaces()
            // view.findViewById<Button>(R.id.change_fragment_button).setOnClickListener{

            //Миша сделай

        //}
    }
}
