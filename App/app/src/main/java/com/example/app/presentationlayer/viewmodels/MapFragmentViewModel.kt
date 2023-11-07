package com.example.app.presentationlayer.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.businesslayer.providers.MapProvider
import com.example.app.datalayer.models.NearbyPlace
import com.example.app.presentationlayer.fragments.mapscreen.MapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

internal class MapFragmentViewModel : ViewModel() {

    private val mapProvider = MapProvider
    lateinit var fragment: MapFragment
    lateinit var placesList: MutableList<NearbyPlace>

    // A default location to use when location permission is not granted. Moscow, Red Square.
    private val defaultLocation = LatLng(55.753544, 37.621202)
    fun onUpdatePlaces() {
        viewModelScope.launch {
            fragment.mainActivity.updateDeviceLocation(
                onSuccess = {
                    viewModelScope.launch {
                        placesList = mapProvider.getSuggestPlaces(
                            it.latitude,
                            it.longitude,
                            20,
                            0
                        ).toMutableList()
                    }
                },
                onFail = {
                    viewModelScope.launch {
                        placesList = mapProvider.getSuggestPlaces(
                            defaultLocation.latitude,
                            defaultLocation.longitude,
                            20,
                            0
                        ).toMutableList()
                    }
                }
            )
        }
    }
    fun getRadius(rad:String){
        mapProvider.updateRadius((rad.toDouble()*1000).toInt())
        onUpdatePlaces()
    }

    fun giveRadius()=((mapProvider.radius).toDouble()/1000).toString()
}