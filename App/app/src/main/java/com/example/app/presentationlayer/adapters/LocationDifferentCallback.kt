package com.example.app.presentationlayer.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.app.datalayer.models.Location

class LocationDifferentCallback: DiffUtil.ItemCallback<Location>() {
    override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Location, newItem:Location): Boolean {
        return oldItem.image_url == newItem.image_url
    }
}