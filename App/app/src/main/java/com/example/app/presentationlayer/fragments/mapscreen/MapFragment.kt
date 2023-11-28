package com.example.app.presentationlayer.fragments.mapscreen

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
import com.example.app.datalayer.models.NearbyPlace
import com.example.app.presentationlayer.MainActivity
import com.example.app.presentationlayer.adapters.PlaceDescriptionImagesSliderRecyclerViewAdapter
import com.example.app.presentationlayer.fragments.placedescriptionscreen.PlaceDescriptionFragment
import com.example.app.presentationlayer.fragments.placepickermapscreen.PlacePickerMapFragment
import com.example.app.presentationlayer.viewmodels.FavoritePlacesViewModel
import com.example.app.presentationlayer.viewmodels.MapFragmentViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.AdvancedMarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
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
import com.google.android.material.tabs.TabLayoutMediator


/**
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding

    private val viewModel by viewModels<MapFragmentViewModel>()

    private val favoritePlacesViewModel by viewModels<FavoritePlacesViewModel>()

    lateinit var mainActivity: MainActivity

    private lateinit var mapFragment: SupportMapFragment

    private lateinit var googleMap: GoogleMap

    private val polylines = mutableListOf<Polyline>()

    private val markers = mutableListOf<Marker>()

    private val markersForRoute = mutableListOf<Marker>()

    private var centerRouteMarker: Marker? = null

    private var previouslyClickedMarker: Marker? = null

    private var previouslyClickedPlace: NearbyPlace? = null

    private var isRouteMode = false

    // Uses for AdvancedMarker
    // Does not work. Bug from google?
    private val pinConfig = PinConfig.builder()
        .setBackgroundColor(Color.WHITE)
        .setBorderColor(Color.WHITE)
        .setGlyph(PinConfig.Glyph("A", Color.WHITE))
        .build()

    private val placeInfoImagesSliderRecyclerViewAdapter =
        PlaceDescriptionImagesSliderRecyclerViewAdapter()

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

        // Нужен true т.к. корневая локация могла измениться из PlacePickerMapFragment
        if (mainActivity.usersLastChosenLocation == mainActivity.usersPreviousChosenLocation) {
            viewModel.onUpdatePlaces(shouldUpdateCachedValue = false)
        } else {
            mainActivity.usersPreviousChosenLocation = mainActivity.usersLastChosenLocation
            viewModel.onUpdatePlaces(shouldUpdateCachedValue = true)
        }

        mapFragment.getMapAsync(this)

        binding.MapFragmentTextViewRadius.text = viewModel.giveRadiusString() + " км"

        binding.MapFragmentButtonRadiusInc.setOnClickListener {
            viewModel.increaseRadius()
            binding.MapFragmentTextViewRadius.text = viewModel.giveRadiusString() + " км"
        }

        binding.MapFragmentButtonRadiusDec.setOnClickListener {
            if (viewModel.decreaseRadius())
                Snackbar.make(
                    binding.MapFragmentButtonRadiusDec,
                    "Установлен минимальный радиус",
                    Snackbar.LENGTH_SHORT
                ).show()
            binding.MapFragmentTextViewRadius.text = viewModel.giveRadiusString() + " км"
        }

        setPlacesListButtonOnClickListener()
        setPlaceDescriptionButtonOnClickListener()
        setRouteButtonOnClickListener()
        setRefreshMapButtonOnClickListener()
        setRemovePlaceButtonOnClickListener()
        setLikeButtonOnClickListener()
        setOnMapClickListener()
        binding.MapFragmentImageViewChooseNewPlace.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.PlacesListRootFragment__FragmentContainerView,
                    PlacePickerMapFragment.newInstance()
                )
                .addToBackStack("PlacePickerMapFragment")
                .commit()
        }

        binding.MapFragmentIncludedPlaceCard.MapFragmentViewPager2PlaceImage.adapter =
            placeInfoImagesSliderRecyclerViewAdapter
        TabLayoutMediator(
            binding.MapFragmentIncludedPlaceCard.PlaceDescriptionFragmentTabLayout,
            binding.MapFragmentIncludedPlaceCard.MapFragmentViewPager2PlaceImage
        ) { tab, position ->
        }.attach()
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

        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                mainActivity.usersLastChosenLocation, DEFAULT_ZOOM.toFloat()
            )
        )
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
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    mainActivity.usersLastChosenLocation, DEFAULT_ZOOM.toFloat()
                )
            )
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

    fun addCenterPlaceMarker() {
        val onMapReadyCallback = OnMapReadyCallback { googleMap ->
            val position = LatLng(
                mainActivity.usersLastChosenLocation.latitude,
                mainActivity.usersLastChosenLocation.longitude,
            )
            val advancedMarkerOptions = AdvancedMarkerOptions()
                .position(position)

            val bitmap = BitmapFactory.decodeResource(
                this.mainActivity.applicationContext.resources,
                R.drawable.map_pin
            )
            googleMap.addMarker(advancedMarkerOptions)?.let {
                markers.add(it)
                centerRouteMarker = it
                it.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))
            }
        }

        mapFragment.getMapAsync(onMapReadyCallback)
    }

    fun addCircleOfRadiusAroundCenterPlace() {
        val onMapReadyCallback = OnMapReadyCallback { googleMap ->
            val circle = CircleOptions()
                .center(mainActivity.usersLastChosenLocation)
                .radius(viewModel.giveRadiusString().toDouble() * 1000)
                .strokeWidth(1.0f)
                //.strokeColor(Color.parseColor("#2271cce7"))
                .fillColor(Color.parseColor("#2271cce7"))
            //.strokeColor(Color.RED)
            //.fillColor(0x220000FF)
            //.strokeWidth(5.0f)

            googleMap.addCircle(circle)
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
            isRouteMode = false

            binding.MapFragmentButtonRoute.visibility = View.VISIBLE
            binding.MapFragmentImageViewRootIcon.visibility = View.VISIBLE
            binding.MapFragmentButtonGoogleRoute.visibility = View.GONE
            binding.MapFragmentImageViewGoogleIcon.visibility = View.GONE
            binding.MapFragmentImageViewRefreshMap.visibility = View.GONE
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
            viewModel.onUpdatePlaces(shouldUpdateCachedValue = false)
        }
    }

    private fun setRemovePlaceButtonOnClickListener() {
        binding.MapFragmentIncludedPlaceCard.MapFragmentButtonRemovePlace.setOnClickListener {
            viewModel.onRemovePlace(previouslyClickedPlace!!)
            val placeExistsInFavoriteDb =
                favoritePlacesViewModel.placeExists(previouslyClickedPlace!!)
            favoritePlacesViewModel.removePlace(previouslyClickedPlace!!)

            Snackbar.make(
                it,
                "${previouslyClickedPlace!!.name} удалено",
                Snackbar.LENGTH_LONG
            ).setAction("Отменить") {
                viewModel.onRestoreRemovedPlace(previouslyClickedPlace!!)
                if (placeExistsInFavoriteDb) {
                    favoritePlacesViewModel.savePlace(previouslyClickedPlace!!)
                }
            }.show()

            hidePlaceInfo()
        }
    }

    private fun setLikeButtonOnClickListener() {
        val likeButton = binding.MapFragmentIncludedPlaceCard.MapFragmentImageViewLike
        likeButton.setOnClickListener {
            // It is necessary to check again whether the place exists
            if (!favoritePlacesViewModel.placeExists(previouslyClickedPlace!!)) {
                favoritePlacesViewModel.savePlace(previouslyClickedPlace!!)
                likeButton.setImageResource(R.drawable.like_liked)
            } else {
                favoritePlacesViewModel.removePlace(previouslyClickedPlace!!)
                likeButton.setImageResource(R.drawable.like_unliked)
            }
        }
    }

    private fun setRouteButtonOnClickListener() {
        binding.MapFragmentButtonRoute.setOnClickListener {
            it.visibility = View.GONE
            binding.MapFragmentImageViewRootIcon.visibility = View.GONE
            binding.MapFragmentButtonGoogleRoute.visibility = View.VISIBLE
            binding.MapFragmentImageViewGoogleIcon.visibility = View.VISIBLE
            binding.MapFragmentImageViewRefreshMap.visibility = View.VISIBLE

            isRouteMode = true

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
                        when (which) {
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

                        // Example: https://www.google.com/maps/dir/?api=1&origin=18.519513,73.868315&destination=18.518496,73.879259&waypoints=18.520561,73.872435|18.519254,73.876614|18.52152,73.877327|18.52019,73.879935&travelmode=driving
                        var requestUrl = "https://www.google.com/maps/dir/?api=1"

                        val startLocation = markersForRoute.first()
                        val endLocation = markersForRoute.last()
                        val startLatLng: LatLng // Dont use val
                        requestUrl += if (shouldUseUserGeolocationAsStartLocation) {
                            val lat = mainActivity.lastKnownLocation?.latitude
                                ?: startLocation.position.latitude
                            val lng = mainActivity.lastKnownLocation?.longitude
                                ?: startLocation.position.longitude
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
                        val endLatLng = LatLng(endLat, endLng)
                        requestUrl += "&destination=$endLat,$endLng"
                        val waypointsLatLng = mutableListOf<LatLng>()
                        val sizeShouldBeMoreThan =
                            if (shouldUseUserGeolocationAsStartLocation) 1 else 2
                        if (markersForRoute.size > sizeShouldBeMoreThan) {
                            requestUrl += "&waypoints="
                            val shouldIgnoreFirstIndex =
                                if (shouldUseUserGeolocationAsStartLocation) -1 else 0
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

                        removeAllPolylines()
                        viewModel.onDrawRoute(startLatLng, endLatLng, waypointsLatLng)
                        if (shouldOpenGoogleMapsNavigator) {

                            launchGoogleMapApp(requestUrl)
                        }
                    }
                    //setNegativeButton("Cancel", null)
                }
                    .create()
                    .show()
            } else {
                val startEndLat = mainActivity.usersLastChosenLocation.latitude
                val startEndLng = mainActivity.usersLastChosenLocation.longitude
                val startLatLng = LatLng(startEndLat, startEndLng)
                val endLatLng = LatLng(startEndLat, startEndLng)

                val waypointsLatLng = mutableListOf<LatLng>()
                markers.forEachIndexed { index, marker ->
                    val lat = marker.position.latitude
                    val lng = marker.position.longitude
                    waypointsLatLng += LatLng(lat, lng)
                }

                removeAllPolylines()
                viewModel.onDrawRoute(startLatLng, endLatLng, waypointsLatLng)
                binding.MapFragmentButtonGoogleRoute.setOnClickListener {
                    viewModel.onGoogleMapRoute(startLatLng, endLatLng, waypointsLatLng)
                }
                /*
                Snackbar.make(
                    binding.MapFragmentImageViewPlaceInfo,
                    "Выберите как минимум одно место двойным кликом",
                    Snackbar.LENGTH_SHORT
                ).show()
                 */
            }
        }
    }

    fun launchGoogleMapApp(requestUrl: String) {
        val intentDeeplink = Uri.parse(requestUrl)
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

    private fun setPlaceDescriptionButtonOnClickListener() {
        binding.MapFragmentIncludedPlaceCard.MapFragmentTextViewPlaceName.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.PlacesListRootFragment__FragmentContainerView,
                    PlaceDescriptionFragment.newInstance(previouslyClickedPlace!!.placeId)
                )
                //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("PlaceDescriptionFragment")
                .commit()
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    fun setOnMarkerClickListener() {
        val onMapReadyCallback = OnMapReadyCallback { googleMap ->
            googleMap.setOnMarkerClickListener {
                if (it != centerRouteMarker) {
                    previouslyClickedPlace = viewModel.getPlaceByLatLng(it.position)

                    showPlaceInfo()

                    if (it == previouslyClickedMarker) {
                        if (!markersForRoute.contains(it)) {
                            it.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                            //it.setIcon(BitmapDescriptorFactory.fromPinConfig(pinConfig))
                            markersForRoute += it
                        } else {
                            markersForRoute -= it
                            it.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        }
                        previouslyClickedMarker = null
                        true

                    } else {
                        previouslyClickedMarker = it
                        false
                    }
                } else {
                    false
                }
            }
        }

        mapFragment.getMapAsync(onMapReadyCallback)
    }

    private fun setOnMapClickListener() {
        val onMapReadyCallback = OnMapReadyCallback { googleMap ->
            googleMap.setOnMapClickListener {
                hidePlaceInfo()
            }
        }

        mapFragment.getMapAsync(onMapReadyCallback)
    }

    private fun hidePlaceInfo() {
        if (binding.PlaceDescriptionFragmentCardViewPlaceInfo.visibility == View.VISIBLE) {
            // Скрываем PlaceInfo с анимацией
            // https://stackoverflow.com/questions/19765938/show-and-hide-a-view-with-a-slide-up-down-animation
            binding.PlaceDescriptionFragmentCardViewPlaceInfo.apply {
                animate()
                    .translationY(-1 * this.height.toFloat())
                    .alpha(0F)
                    .setListener(
                        object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                this@apply.visibility = View.GONE
                            }
                        }
                    )
            }

            showHideTopButtons(true)
        }
    }

    private fun showPlaceInfo() {
        if (previouslyClickedPlace != null) {
            setPlaceInfoData()

            showHideTopButtons(false)

            // Показываем PlaceInfo с анимацией
            // https://stackoverflow.com/questions/19765938/show-and-hide-a-view-with-a-slide-up-down-animation
            binding.PlaceDescriptionFragmentCardViewPlaceInfo.alpha = 0F
            binding.PlaceDescriptionFragmentCardViewPlaceInfo.visibility = View.VISIBLE
            binding.PlaceDescriptionFragmentCardViewPlaceInfo.apply {
                animate()
                    .translationY(0F)
                    .alpha(1F)
                    .setListener(null)
            }
        } else {
            Snackbar.make(
                binding.MapFragmentImageViewPlaceInfo,
                "Не удалось получить описание",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * @param [shouldShow] true - show top buttons, false - hide top buttons.
     */
    private fun showHideTopButtons(shouldShow: Boolean) {
        val visibilityFlag = if (shouldShow) View.VISIBLE else View.GONE

        // TODO объединить эти кнопки в группу и показывать сразу группу
        binding.MapFragmentImageViewChooseNewPlace.visibility = visibilityFlag
        binding.MapFragmentButtonRadiusDec.visibility = visibilityFlag
        binding.MapFragmentButtonRadiusInc.visibility = visibilityFlag
        binding.radiusBack.visibility = visibilityFlag
        binding.MapFragmentTextViewRadius.visibility = visibilityFlag
        binding.MapFragmentTextViewRadiusTxt.visibility = visibilityFlag
        binding.MapFragmentImageViewRefreshMap.visibility =
            if (isRouteMode && shouldShow) View.VISIBLE else View.GONE
    }

    private fun setPlaceInfoData() {
        binding.MapFragmentIncludedPlaceCard.MapFragmentTextViewPlaceName.text =
            previouslyClickedPlace!!.name

        // Чтобы на мгновение не показывались фотки с предыдущего кликнутого места
        placeInfoImagesSliderRecyclerViewAdapter.submitList(emptyList())

        viewModel.updateImageSlider(
            previouslyClickedPlace!!.placeId,
            placeInfoImagesSliderRecyclerViewAdapter
        )
        binding.MapFragmentIncludedPlaceCard.MapFragmentRatingBarStarsRate.rating =
            previouslyClickedPlace!!.rating.toFloat()
        binding.MapFragmentIncludedPlaceCard.MapFragmentTextViewRate.text =
            previouslyClickedPlace!!.rating.toString()
        binding.MapFragmentIncludedPlaceCard.MapFragmentTextViewRateCount.text =
            previouslyClickedPlace!!.ratingCount.toString()


        // Likes
        val likeButton = binding.MapFragmentIncludedPlaceCard.MapFragmentImageViewLike
        if (favoritePlacesViewModel.placeExists(previouslyClickedPlace!!)) {
            likeButton.setImageResource(R.drawable.like_liked)
        } else {
            likeButton.setImageResource(R.drawable.like_unliked)
        }
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

        private const val LOG_TAG = "MapFragment"

        private const val DEFAULT_ZOOM = 12

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
