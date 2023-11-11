package com.example.app.presentationlayer

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.app.databinding.ActivityMainBinding
import com.example.app.datalayer.models.NearbyPlace
import com.example.app.datalayer.repositories.LocalPropertiesSecretsRepository
import com.example.app.domain.providers.MapAndroidClient
import com.example.app.presentationlayer.adapters.TabBarAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import io.paperdb.Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var locationPermissionGranted = false


    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    var lastKnownLocation: Location? = null

    var onLocationPermissionGrantedForMapFragment: () -> Unit =
        {} // Initializes from MapFragment
    var onLocationPermissionGrantedForPlacesListFragment: (forceRefresh: Boolean) -> Unit =
        {} // Initializes from PlacesListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        LocalPropertiesSecretsRepository.APP_PACKAGE_NAME = applicationContext.packageName
        // !!! All requests to backend (such as ping-pong) should be used after this
        // because uuid sets to header and we can get exception uninitialized uuid
        generateOrInitializeUserUUID()
        setupTabBar()
        initMapAndroidClient()
        initLocationClient()

        requestLocationPermission()

        Paper.init(applicationContext);
    }

    private fun generateOrInitializeUserUUID() {
        val sharedPreferences = this.getPreferences(MODE_PRIVATE)
        val userUUID = sharedPreferences.getString(USER_UUID_KEY, "")

        LocalPropertiesSecretsRepository.USER_UUID =
            if (!userUUID.isNullOrEmpty()) {
                userUUID
            } else {
                // The first launch
                val newUserUUID = UUID.randomUUID().toString()

                val editor = sharedPreferences.edit()
                editor.putString(USER_UUID_KEY, newUserUUID)
                val isSuccess = editor.commit()
                if (!isSuccess) {
                    throw RuntimeException("Error while saving user UUID!")
                }

                newUserUUID
            }
    }

    private fun setupTabBar() {
        // Disable swipe between fragments
        binding.MainActivityViewPager.isUserInputEnabled = false

        binding.MainActivityViewPager.adapter =
            TabBarAdapter(this, binding.MainActivityTabLayout.tabCount)

        binding.MainActivityViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.MainActivityTabLayout.selectTab(
                        binding.MainActivityTabLayout.getTabAt(position)
                    )
                }
            }
        )

        binding.MainActivityTabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    binding.MainActivityViewPager.setCurrentItem(tab.position, false)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            }
        )
    }

    private fun initMapAndroidClient() {
        Places.initialize(applicationContext, LocalPropertiesSecretsRepository.MAPS_API_KEY)
        MapAndroidClient.placesClient = Places.createClient(this)
    }

    private fun initLocationClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    /**
     * Prompts the user for permission to use the device location.
     * Request location permission, so that we can get the location of the device.
     * The result of the permission request is handled by a onRequestPermissionsResult callback.
     */
    fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false

        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                } else {
                    Snackbar.make(
                        binding.MainActivityViewPager,
                        "Включите разрешение на геолокацию в 'настройки ->приложения->наше приложение->разрешения'",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

        // TODO check is fragment instance exists or/and visible or/and attached
        onLocationPermissionGrantedForMapFragment()
        onLocationPermissionGrantedForPlacesListFragment(true)
    }

    /**
     * Gets the current location of the device.
     */
    fun updateDeviceLocation(
        onSuccess: (location: Location) -> Unit,
        onFail: () -> Unit,
    ) {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful && task.result != null) {
                        // Set the map's camera position to the current location of the device.
                        val lastKnownLocation = task.result
                        this.lastKnownLocation = lastKnownLocation
                        onSuccess(lastKnownLocation)
                    } else {
                        Log.e(LOG_TAG, "Exception: %s", task.exception)
                        onFail()
                    }
                }
            } else {
                onFail()
            }
        } catch (e: SecurityException) {
            Log.e(LOG_TAG, e.message, e)
            onFail()
        }
    }

    fun saveLikedPlace(place: NearbyPlace) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                Paper.book("liked_places").write(place.placeId, place)
                Paper.book("liked_places").read<NearbyPlace>(place.placeId)
            }
        }
    }

    fun deleteLikedPlace(place: NearbyPlace) {
        Paper.book("liked_places").delete(place.placeId)
    }

    fun likeCheck(place: NearbyPlace) =
        Paper.book("liked_places").read<NearbyPlace>(place.placeId) != null


    fun getLikedPlace(): MutableList<NearbyPlace> {
        val list: MutableList<NearbyPlace> = emptyList<NearbyPlace>().toMutableList()
        var place: NearbyPlace
        Paper.book("liked_places").allKeys.forEach {
            place = Paper.book("liked_places").read<NearbyPlace>(it)!!
            list += place
        }
        return list.toMutableList()
    }

    companion object {

        private const val LOG_TAG = "MainActivity"

        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        private const val USER_UUID_KEY = "user_uuid_key"
    }
}