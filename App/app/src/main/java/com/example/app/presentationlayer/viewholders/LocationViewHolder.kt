package com.example.app.presentationlayer.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.app.R
import com.example.app.datalayer.models.Location

class LocationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val image by lazy { view.findViewById<ImageView>(R.id.location_avatar) }
    private val name_txt = view.findViewById<TextView>(R.id.location_name)


    fun bind(location: Location) {
        val url = location.imageUrl()
        val name = location.name()
        name_txt.text = name
        image.load(url)

    }

}