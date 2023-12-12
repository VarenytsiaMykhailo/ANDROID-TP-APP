package com.example.app.presentationlayer.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import coil.load
import com.example.app.BuildConfig
import com.example.app.R

internal class PlaceDescriptionImagesSliderRecyclerViewAdapter(
    private val context: Context,
) : ListAdapter<String, PlaceDescriptionImagesSliderRecyclerViewAdapter.ImageCardViewHolder>(
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
        holder.bind(url, context)
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

        @OptIn(ExperimentalCoilApi::class)
        fun bind(imageUrl: String, context: Context) {
            imageView.load(imageUrl)
            imageView.setOnClickListener {
//                val intent = Intent()
//                intent.action = Intent.ACTION_VIEW
//                intent.setDataAndType(
//                    Uri.parse(imageUrl),
//                    "image/*"
//                )
                context.imageLoader.diskCache?.openSnapshot(imageUrl)?.use { snapshot ->
                    val imageFile = snapshot.data.toFile()
                    //val uri = Uri.fromFile(imageFile)
                    val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", imageFile);

                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.setDataAndType(
                        uri,
                        "image/*"
                    )
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(itemView.context, intent, null)
                }

//                val uri =
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(imageUrl)
//                        .build()
//                val intent = Intent()
//                intent.action = Intent.ACTION_VIEW
//                intent.setDataAndType(
//                    Uri.parse("https://cdn.fotosklad.ru/unsafe/24d33472d338457ebe3ed0f8cc0ba6a9/image.jpg"),
//                    "image/*"
//                )
//                startActivity(itemView.context, intent, null)
            }
        }
    }
}
