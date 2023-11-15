package com.example.app.presentationlayer.fragments.mapscreen

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.app.R
import com.example.app.databinding.FragmentMapBinding
import com.example.app.presentationlayer.MainActivity
import com.example.app.presentationlayer.fragments.placedescriptionscreen.PlaceDescriptionFragment
import com.example.app.presentationlayer.viewmodels.MapFragmentViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.AdvancedMarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PatternItem
import com.google.android.gms.maps.model.PinConfig
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

    private lateinit var mainActivity: MainActivity

    private lateinit var mapFragment: SupportMapFragment

    private lateinit var googleMap: GoogleMap

    private val polylines = mutableListOf<Polyline>()

    private val markers = mutableListOf<Marker>()

    private val markersForRoute = mutableListOf<Marker>()

    private var previouslyClickedMarker: Marker? = null

    // Uses for AdvancedMarker
    // Does not work. Bug from google?
    private val pinConfig = PinConfig.builder()
        .setBackgroundColor(Color.WHITE)
        .setBorderColor(Color.WHITE)
        .setGlyph(PinConfig.Glyph("A", Color.WHITE))
        .build()

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

        setPlacesListButtonOnClickListener()
        setPlaceInfoButtonOnClickListener()
        setRouteButtonOnClickListener()
        setRefreshMapButtonOnClickListener()
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map

        mainActivity.requestLocationPermission()

        setOnMarkerClickListener()

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

    fun addAdvancedMarker(
        latitude: Double,
        longitude: Double,
        // default value should be " " because snippet does not show with empty string
        placeTitle: String = " ",
        placeDescription: String = "",
    ) {
        val onMapReadyCallback = OnMapReadyCallback { googleMap ->
            val position = LatLng(latitude, longitude)
            val advancedMarkerOptions = AdvancedMarkerOptions()
                .position(position)
                .title(placeTitle)
                .apply {
                    if (placeDescription.isNotBlank()) {
                        snippet(placeDescription)
                    }
                }

            googleMap.addMarker(advancedMarkerOptions)?.let {
                markers.add(it)
                it.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }
            Log.d("qwerty123", "markers.size = ${markers.size} markers = $markers")
        }

        mapFragment.getMapAsync(onMapReadyCallback)
    }

    fun onDrawRoute(
        pointsList: List<LatLng>,
    ) {
        val onMapReadyCallback = OnMapReadyCallback { googleMap ->
            val locationStart = pointsList[0]
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(locationStart, DEFAULT_ZOOM.toFloat())
            )

            //val pointsList = PolyUtil.decode("ipkcFjgchVd@@@cF]@@oCK?")
            //Log.d("qwerty123", "$pointsList")

            val routePolyline = googleMap.addPolyline(
                PolylineOptions()
                    .addAll(pointsList)
            )
            routePolyline.tag = "routePolylineWithArrow"
            stylePolyline(routePolyline)
            polylines += routePolyline
        }

        mapFragment.getMapAsync(onMapReadyCallback)
    }

    private fun stylePolyline(polyline: Polyline) {
        when (polyline.tag?.toString() ?: "") {
            "routePolylineWithArrow" -> {
                polyline.startCap = RoundCap()
            }

            else -> polyline.startCap = RoundCap()
        }
        /*
        polyline.endCap = CustomCap(
            BitmapDescriptorFactory.fromResource(R.drawable.flag), 10f
        )
         */
        val flagBitmap = BitmapFactory.decodeResource(
            this.mainActivity.applicationContext.resources,
            R.drawable.flag
        )
        //markersForRoute.last().setIcon(BitmapDescriptorFactory.fromBitmap(flagBitmap))
        polyline.width = 12.0F
        polyline.color = ContextCompat.getColor(requireContext(), R.color.route_polyline_color)
        polyline.jointType = JointType.ROUND

        val patternGapLengthPx = 20
        val dot: PatternItem = Dot()
        val gap: PatternItem = Gap(patternGapLengthPx.toFloat())
        val patternPolylineDotted = listOf(gap, dot)
        polyline.pattern = patternPolylineDotted
    }

    private fun removeAllPolylines() {
        polylines.forEach {
            it.remove()
        }
        polylines.clear()
    }

    private fun removeAllMarkers() {
        markers.forEach {
            it.remove()
        }
        markers.clear()
        markersForRoute.forEach {
            it.remove()
        }
        markersForRoute.clear()
    }

    fun refreshMap() {
        val onMapReadyCallback = OnMapReadyCallback { googleMap ->
            googleMap.clear()
            removeAllMarkers()
            removeAllPolylines()
        }

        mapFragment.getMapAsync(onMapReadyCallback)
    }

    private fun setPlacesListButtonOnClickListener() {
        // TODO переделать чтобы можно было свитчиться между фрагментами с сохранением состояния
        binding.MapFragmentImageViewPlacesListButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setRefreshMapButtonOnClickListener() {
        binding.MapFragmentImageViewRefreshMap.setOnClickListener {
            viewModel.onUpdatePlaces(shouldRefreshMapBefore = true)
        }
    }

    private fun setRouteButtonOnClickListener() {
        binding.RouteButton.setOnClickListener {
            if (markersForRoute.size >= 1) {

                var shouldUseUserSequence = true
                var shouldUseUserGeolocationAsStartLocation = false
                var shouldOpenGoogleMapsNavigator = false

                AlertDialog.Builder(this.requireContext()).apply {
                    setTitle("Настройка маршрута")
                    setMultiChoiceItems(
                        arrayOf(
                            "Маршрут по порядку выбранных точек (в противном случае будет выбран оптимальный маршрут)",
                            "Начать с местоположения пользователя",
                            "Открыть навигатор Google Maps"
                        ),
                        booleanArrayOf(true, false, false)
                    ) { dialog, which, isChecked ->
                        // The user checked or unchecked a box
                        Log.d("qwerty123", "which = $which, isChecked = $isChecked")
                        when(which) {
                            0 -> shouldUseUserSequence = isChecked
                            1 -> shouldUseUserGeolocationAsStartLocation = isChecked
                            2 -> shouldOpenGoogleMapsNavigator = isChecked
                        }
                    }
                    setPositiveButton("OK") { dialog, which ->
                        if (markersForRoute.size == 1 && !shouldUseUserGeolocationAsStartLocation) {
                            Snackbar.make(
                                binding.MapFragmentImageViewPlaceInfo,
                                "Выберите как минимум два места двойным кликом или начните с местоположения",
                                Snackbar.LENGTH_SHORT
                            ).show()

                            return@setPositiveButton
                        }

                        Log.d("qwerty123", "shouldUseUserSequence = $shouldUseUserSequence")
                        Log.d("qwerty123", "shouldUseUserGeolocationAsStartLocation = $shouldUseUserGeolocationAsStartLocation")
                        // Example: "https://www.google.com/maps/dir/?api=1&origin=18.519513,73.868315&destination=18.518496,73.879259&waypoints=18.520561,73.872435|18.519254,73.876614|18.52152,73.877327|18.52019,73.879935&travelmode=driving"
                        var requestUrl = "https://www.google.com/maps/dir/?api=1"

                        val startLocation = markersForRoute.first()
                        val endLocation = markersForRoute.last()
                        var startLatLng: LatLng // Dont use val
                        requestUrl += if (shouldUseUserGeolocationAsStartLocation) {
                            val lat = mainActivity.lastKnownLocation?.latitude ?: startLocation.position.latitude
                            val lng = mainActivity.lastKnownLocation?.longitude ?: startLocation.position.longitude
                            startLatLng = LatLng(lat, lng)
                            "&origin=$lat,$lng"
                        } else {
                            val lat = startLocation.position.latitude
                            val lng = startLocation.position.longitude
                            startLatLng = LatLng(lat, lng)
                            "&origin=$lat,$lng"
                        }
                        val endLat = endLocation.position.latitude
                        val endLng = endLocation.position.longitude
                        val endLatLng: LatLng = LatLng(endLat, endLng)
                        requestUrl += "&destination=$endLat,$endLng"
                        val waypointsLatLng = mutableListOf<LatLng>()
                        val sizeShouldBeMoreThan = if (shouldUseUserGeolocationAsStartLocation) 1 else 2
                        if (markersForRoute.size > sizeShouldBeMoreThan) {
                            requestUrl += "&waypoints="
                            val shouldIgnoreFirstIndex = if (shouldUseUserGeolocationAsStartLocation) -1 else 0
                            markersForRoute.forEachIndexed { index, marker ->
                                if (index != shouldIgnoreFirstIndex && index != markersForRoute.size - 1) {
                                    val lat = marker.position.latitude
                                    val lng = marker.position.longitude
                                    waypointsLatLng += LatLng(lat, lng)
                                    requestUrl += "$lat,$lng"
                                    if (index != markersForRoute.size - 2) {
                                        requestUrl += "|"
                                    }
                                }
                            }
                        }
                        requestUrl += "&travelmode=walk"
                        Log.d("qwerty123", "requestUrl = $requestUrl")
                        Log.d("qwerty123", "startLatLng = $startLatLng endLatLng = $endLatLng waypointsLatLng = $waypointsLatLng")

                        removeAllPolylines()
                        viewModel.onDrawRoute(startLatLng, endLatLng, waypointsLatLng)
                        if (shouldOpenGoogleMapsNavigator) {
                            val intentDeeplink = Uri.parse(requestUrl)
                            launchGoogleMapApp(intentDeeplink)
                        }
                    }
                    //setNegativeButton("Cancel", null)
                }
                    .create()
                    .show()
            } else {
                Snackbar.make(
                    binding.MapFragmentImageViewPlaceInfo,
                    "Выберите как минимум одно место двойным кликом",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun launchGoogleMapApp(intentDeeplink: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, intentDeeplink)
        intent.setPackage("com.google.android.apps.maps")
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e(LOG_TAG, "outer catch e = $e")
            try {
                val unrestrictedIntent = Intent(Intent.ACTION_VIEW, intentDeeplink)
                startActivity(unrestrictedIntent)
            } catch (e: ActivityNotFoundException) {
                Log.e(LOG_TAG, "inner catch e = $e")
                Snackbar.make(
                    binding.MapFragmentImageViewPlaceInfo,
                    "Установите приложение Google Maps",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setPlaceInfoButtonOnClickListener() {
        binding.MapFragmentImageViewPlaceInfo.setOnClickListener {
            if (previouslyClickedMarker != null) {
                val placeId = viewModel.getPlaceIdByLatLng(
                    previouslyClickedMarker!!.position
                )
                if (placeId != null) {
                    parentFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.PlacesListRootFragment__FragmentContainerView,
                            PlaceDescriptionFragment.newInstance(placeId)
                        )
                        //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack("PlaceDescriptionFragment")
                        .commit()
                } else {
                    Snackbar.make(
                        binding.MapFragmentImageViewPlaceInfo,
                        "Не удалось получить описание",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } else {
                Snackbar.make(
                    binding.MapFragmentImageViewPlaceInfo,
                    "Выберите место",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    fun setOnMarkerClickListener() {
        val onMapReadyCallback = OnMapReadyCallback { googleMap ->
            googleMap.setOnMarkerClickListener {

                Log.d("qwerty123", "setOnMarkerClickListener enter")
                if (it == previouslyClickedMarker) {
                    if (!markersForRoute.contains(it)) {
                        Log.d("qwerty123", "setOnMarkerClickListener !markersForRoute.contains(it)")
                        it.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        //it.setIcon(BitmapDescriptorFactory.fromPinConfig(pinConfig))
                        markersForRoute += it
                    } else {
                        Log.d("qwerty123", "setOnMarkerClickListener markersForRoute.contains(it)")
                        markersForRoute -= it
                        it.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    }
                    previouslyClickedMarker = null
                    Log.d("qwerty123", "markersForRoute $markersForRoute")
                    true

                } else {
                    previouslyClickedMarker = it
                    Log.d("qwerty123", "setOnMarkerClickListener (clicksCount == 1)")
                    Log.d("qwerty123", "markersForRoute $markersForRoute")
                    false
                }
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
     * @return A new instance of fragment MapFragment.
     */
    companion object {

        private const val DEFAULT_ZOOM = 14

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
