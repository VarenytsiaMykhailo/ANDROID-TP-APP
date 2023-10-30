package com.example.app.presentationlayer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.app.R

internal class PlaceDescriptionImagesSliderRecyclerViewAdapter :
    ListAdapter<String, PlaceDescriptionImagesSliderRecyclerViewAdapter.ImageCardViewHolder>(StringDifferentCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageCardViewHolder {
        val view: View =
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.slider_item, parent, false)

        return ImageCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageCardViewHolder, position: Int) {
        val url = getItem(position)
        holder.bind(url)
    }

    private class StringDifferentCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String) =
            oldItem == newItem
    }

    internal class ImageCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView =
            itemView.findViewById<ImageView>(R.id.PlaceDescriptionFragment__ImageView_PlaceImage)

        fun bind(imageUrl: String) {
            imageView.load(imageUrl)
        }
    }
}
