package com.example.app.presentationlayer.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.datalayer.models.PlaceDescription
import com.example.app.datalayer.models.PlaceReaction
import com.example.app.domain.providers.MapProvider
import com.example.app.presentationlayer.fragments.placedescriptionscreen.PlaceDescriptionFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class PlaceDescriptionFragmentViewModel : ViewModel() {

    private val mapProvider = MapProvider

    lateinit var fragment: PlaceDescriptionFragment

    val place: Flow<PlaceDescription> = flow {
        emit(mapProvider.getPlaceDescription(fragment.placeID))
    }

    /*
    var staticMapImageUrl = "https://maps.googleapis.com/maps/api/staticmap?center="
    staticMapImageUrl += "${location.lat},${location.lng}"
    staticMapImageUrl += "&zoom=13&size=700x350&markers=color:red%7Clabel:S%7C"
    staticMapImageUrl += "${location.lat},${location.lng}"
    staticMapImageUrl += "&key=${LocalPropertiesSecretsRepository.MAPS_API_KEY}"

    val googleMapAppDeeplink =
        "http://maps.google.com/maps?q=loc:${location.lat},${location.lng}"
    */

    fun postReaction(placeId: String, reaction: PlaceReaction.Reaction) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                mapProvider.postSuggestReaction(placeId, reaction)
            }
        }
    }
}