package com.example.app.presentationlayer.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.datalayer.models.PlaceDescription
import com.example.app.domain.providers.MapProvider
import com.example.app.presentationlayer.adapters.PlaceDescriptionImagesSliderRecyclerViewAdapter
import com.example.app.presentationlayer.fragments.placedescriptionscreen.PlaceDescriptionFragment
import kotlinx.coroutines.launch

internal class PlaceDescriptionFragmentViewModel : ViewModel() {

    private val mapProvider = MapProvider

    lateinit var fragment: PlaceDescriptionFragment

    var placeDescriptionImagesSliderRecyclerViewAdapter: PlaceDescriptionImagesSliderRecyclerViewAdapter? =
        null

    fun onSetContent(placeId: String) {
        viewModelScope.launch {
            val placeDescription = mapProvider.getPlaceDescription(placeId)

            updateImagesSlider(placeDescription.photos)
            setTitle(placeDescription.name)
            setDescription(placeDescription.description)
            setRating(placeDescription.rating)
            setRatingCount(placeDescription.ratingCount)
            setMap(placeDescription)
            setAddress(placeDescription.address)
            setWorkingHours(placeDescription.workingHours)
        }
    }

    private fun updateImagesSlider(imageUrls: List<String>) =
        placeDescriptionImagesSliderRecyclerViewAdapter?.submitList(imageUrls)

    private fun setTitle(title: String) =
        fragment.onSetTitle(title)

    private fun setDescription(description: String) =
        // TODO Вынести табуляюцию текста в usecase
        fragment.onSetDescription("      $description")

    private fun setRating(rating: Double) =
        fragment.onSetRating(rating)

    private fun setRatingCount(rating: Int) =
        fragment.onSetRatingCount(rating)

    private fun setMap(placeDescription: PlaceDescription) {
        /*
        var staticMapImageUrl = "https://maps.googleapis.com/maps/api/staticmap?center="
        staticMapImageUrl += "${location.lat},${location.lng}"
        staticMapImageUrl += "&zoom=13&size=700x350&markers=color:red%7Clabel:S%7C"
        staticMapImageUrl += "${location.lat},${location.lng}"
        staticMapImageUrl += "&key=${LocalPropertiesSecretsRepository.MAPS_API_KEY}"

        val googleMapAppDeeplink =
            "http://maps.google.com/maps?q=loc:${location.lat},${location.lng}"
        */

        fragment.onSetMap(placeDescription)
    }

    private fun setAddress(address: String) =
        fragment.onSetAddress(address)

    private fun setWorkingHours(workingHours: List<String>) {
        // TODO Вынести преобразование списка в строку в usecase
        val workingHoursString = workingHours.joinToString("\n")
        fragment.onSetWorkingHours(workingHoursString)
    }
}