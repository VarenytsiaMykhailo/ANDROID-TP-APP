package com.example.app.presentationlayer.adapters

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.app.R

internal class PlaceDescriptionImagesSliderRecyclerViewAdapter :
    ListAdapter<String, PlaceDescriptionImagesSliderRecyclerViewAdapter.ImageCardViewHolder>(
        StringDifferentCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageCardViewHolder =
        ImageCardViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.place_description_slider_item, parent, false)
        )


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
            imageView.setOnClickListener {
//                val intent = Intent()
//                intent.action = Intent.ACTION_VIEW
//                intent.setDataAndType(
//                    Uri.parse(imageUrl),
//                    "image/*"
//                )
                val uri =
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(imageUrl)
                        .build()
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.setDataAndType(
                    Uri.parse("https://cdn.fotosklad.ru/unsafe/24d33472d338457ebe3ed0f8cc0ba6a9/image.jpg"),
                    "image/*"
                )
                startActivity(itemView.context, intent, null)
            }
        }
    }
}
