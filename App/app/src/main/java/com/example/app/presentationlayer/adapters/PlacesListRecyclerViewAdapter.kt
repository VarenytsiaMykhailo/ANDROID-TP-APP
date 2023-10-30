package com.example.app.presentationlayer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.app.R
import com.example.app.datalayer.models.Place

internal class PlacesListRecyclerViewAdapter(
    private val launchPlaceDescriptionFragment: (placeId: String) -> Unit,
) : ListAdapter<Place, PlacesListRecyclerViewAdapter.PlaceViewHolder>(LocationDifferentCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.location_card_layout, parent, false)

        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = getItem(position)
        holder.bind(place, launchPlaceDescriptionFragment)
    }

    private class LocationDifferentCallback : DiffUtil.ItemCallback<Place>() {
        override fun areItemsTheSame(oldItem: Place, newItem: Place) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Place, newItem: Place) =
            oldItem.mainImageUrl == newItem.mainImageUrl
    }
}

internal class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val expandableInfo = view.findViewById<ConstraintLayout>(R.id.expandable_info)

    private val mainImage by lazy { view.findViewById<ImageView>(R.id.location_main_picture) }
    private val image1 by lazy { view.findViewById<ImageView>(R.id.location_picture_1) }
    private val image2 by lazy { view.findViewById<ImageView>(R.id.location_picture_2) }
    private val image3 by lazy { view.findViewById<ImageView>(R.id.location_picture_3) }

    private val placeNameWhite = view.findViewById<TextView>(R.id.location_name_white)
    private val ratingWhite = view.findViewById<TextView>(R.id.location_rating_white)

    private val placeName = view.findViewById<TextView>(R.id.location_name)
    private val rating = view.findViewById<TextView>(R.id.location_rating)
    private val ratingStars = view.findViewById<RatingBar>(R.id.location_stars_rating)
    private val ratingCount = view.findViewById<TextView>(R.id.location_rating_count)

    private val placeDescription = view.findViewById<TextView>(R.id.location_description)

    fun bind(place: Place) {
        placeNameWhite.text = place.name
        ratingWhite.text = place.rating.toString()

        placeName.text = place.name
        rating.text = place.rating.toString()
        ratingStars.rating = place.rating.toFloat()
        ratingCount.text = place.ratingCount.toString()

        placeDescription.text = place.name

        mainImage.load(place.mainImageUrl)
        image1.load(place.mainImageUrl)
        image2.load(place.mainImageUrl)
        image3.load(place.mainImageUrl)

        var expandable = true
        mainImage.setOnClickListener {
            if (expandable) {
                expandableInfo.visibility = View.VISIBLE
                placeNameWhite.visibility = View.GONE
                ratingWhite.visibility = View.GONE
                expandable = !expandable
            } else {
                expandableInfo.visibility = View.GONE
                placeNameWhite.visibility = View.VISIBLE
                ratingWhite.visibility = View.VISIBLE
                expandable = !expandable
            }
        }
        placeName.setOnClickListener {
            launchPlaceDescriptionFragment(place.placeId)
        }
    }
}
