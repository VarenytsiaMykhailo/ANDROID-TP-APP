package com.example.app.presentationlayer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.app.R
import com.example.app.datalayer.models.Place

internal class PlacesListRecyclerViewAdapter :
    ListAdapter<Place, LocationViewHolder>(LocationDifferentCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_card_layout, parent, false)

        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = getItem(position)
        holder.bind(location)
    }

    private class LocationDifferentCallback : DiffUtil.ItemCallback<Place>() {
        override fun areItemsTheSame(oldItem: Place, newItem: Place) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Place, newItem: Place) =
            oldItem.imageUrl == newItem.imageUrl
    }
}

internal class LocationViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val image by lazy { view.findViewById<ImageView>(R.id.location_avatar) }

    private val placeName = view.findViewById<TextView>(R.id.location_name)

    fun bind(place: Place) {
        placeName.text = place.name
        image.load(place.imageUrl)
    }
}