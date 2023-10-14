package com.example.app.presentationlayer.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.app.R
import com.example.app.datalayer.Models.Location
import com.example.app.presentationlayer.ViewHolders.LocationViewHolder

class LocationAdapter: ListAdapter<Location, LocationViewHolder>(LocationDifferentCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.location_card_layout, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = getItem(position)
        holder.bind(location)
    }
}