package com.example.app.presentationlayer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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
            oldItem.imageUrl == newItem.imageUrl
    }

    internal class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imageView by lazy { view.findViewById<ImageView>(R.id.location_avatar) }

        private val placeNameTextView = view.findViewById<TextView>(R.id.location_name)

        private val locationCard: ConstraintLayout =
            view.findViewById(R.id.LocationCard)

        fun bind(
            place: Place,
            launchPlaceDescriptionFragment: (placeId: String) -> Unit,
        ) {
            placeNameTextView.text = place.name
            imageView.load(place.imageUrl)

            locationCard.setOnClickListener {
                launchPlaceDescriptionFragment(place.placeId)
            }
        }
    }
}

