package com.example.app.presentationlayer.fragments.placedescriptionscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.app.R
import com.example.app.databinding.FragmentSmallMapBinding
import com.example.app.presentationlayer.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.AdvancedMarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

/**
 * Use the [SmallMapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SmallMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentSmallMapBinding

    private lateinit var mainActivity: MainActivity

    private lateinit var mapFragment: SupportMapFragment

    private lateinit var googleMap: GoogleMap

    private val markers = mutableListOf<Marker>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSmallMapBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = requireActivity() as MainActivity


        mapFragment =
            childFragmentManager.findFragmentById(R.id.SmallMapFragment__FragmentContainerView) as SupportMapFragment

        mapFragment.getMapAsync(this)


    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map

        val lat = requireArguments().getString(LAT)!!
        val lng = requireArguments().getString(LNG)!!
        val location = LatLng(lat.toDouble(), lng.toDouble())

        googleMap.setOnCameraMoveListener { (parentFragment as PlaceDescriptionFragment).disableScroll() }

        addAdvancedMarker(location.latitude, location.longitude)
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location.latitude,
                    location.longitude
                ), DEFAULT_ZOOM.toFloat()
            )
        )
    }

    private fun addAdvancedMarker(
        latitude: Double,
        longitude: Double,
    ) {
        val onMapReadyCallback = OnMapReadyCallback { googleMap ->
            val position = LatLng(latitude, longitude)
            val advancedMarkerOptions = AdvancedMarkerOptions()
                .position(position)

            googleMap.addMarker(advancedMarkerOptions)?.let {
                markers.add(it)
                it.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }
        }

        mapFragment.getMapAsync(onMapReadyCallback)
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

        private const val LOG_TAG = "SmallMapFragment"

        private const val DEFAULT_ZOOM = 11

        private const val LAT = "lat"

        private const val LNG = "lng"
        private const val ID = "id"

        @JvmStatic
        fun newInstance(
            lat: String,
            lng: String,
        ) = SmallMapFragment().apply {
            arguments = Bundle().apply {
                putString(LAT, lat)
                putString(LNG, lng)
            }
        }
    }
}