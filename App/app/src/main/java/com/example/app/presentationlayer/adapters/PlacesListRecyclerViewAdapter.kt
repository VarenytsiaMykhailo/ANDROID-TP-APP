package com.example.app.presentationlayer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.app.R
import com.example.app.datalayer.models.NearbyPlace

internal class PlacesListRecyclerViewAdapter(
    private val launchPlaceDescriptionFragment: (placeId: String) -> Unit,
) : ListAdapter<NearbyPlace, PlacesListRecyclerViewAdapter.PlaceViewHolder>(
    LocationDifferentCallback()
) {

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

    private class LocationDifferentCallback : DiffUtil.ItemCallback<NearbyPlace>() {
        override fun areItemsTheSame(oldItem: NearbyPlace, newItem: NearbyPlace) =
            oldItem.placeId == newItem.placeId

        override fun areContentsTheSame(oldItem: NearbyPlace, newItem: NearbyPlace) =
            oldItem.mainImageUrl == newItem.mainImageUrl // TODO все ли проверки тут выполнены?
    }

    internal class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val expandableInfo =
            view.findViewById<ConstraintLayout>(R.id.PlacesListFragment__ConstraintLayout_ExpandableInfo)

        private val mainImage =
            view.findViewById<ImageView>(R.id.PlacesListFragment__ImageView_MainPic)
        private val image1 =
            view.findViewById<ImageView>(R.id.PlacesListFragment__ImageView_Pic1)
        private val image2 =
            view.findViewById<ImageView>(R.id.PlacesListFragment__ImageView_Pic2)
        private val image3 =
            view.findViewById<ImageView>(R.id.PlacesListFragment__ImageView_Pic3)

        private val placeNameWhite =
            view.findViewById<TextView>(R.id.PlacesListFragment__TextView_NameWhite)
        private val ratingWhite =
            view.findViewById<TextView>(R.id.PlacesListFragment__TextView_RateWhite)

        private val placeName =
            view.findViewById<TextView>(R.id.PlacesListFragment__TextView_Name)
        private val rating =
            view.findViewById<TextView>(R.id.PlacesListFragment__TextView_Rate)
        private val ratingStars =
            view.findViewById<RatingBar>(R.id.PlacesListFragment__RatingBar_StarsRate)
        private val ratingCount =
            view.findViewById<TextView>(R.id.PlacesListFragment__TextView_RateCount)

        private val placeDescription =
            view.findViewById<TextView>(R.id.PlacesListFragment__TextView_Description)

        fun bind(
            place: NearbyPlace,
            launchPlaceDescriptionFragment: (placeId: String) -> Unit,
        ) {
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

                } else {
                    expandableInfo.visibility = View.GONE
                    placeNameWhite.visibility = View.VISIBLE
                    ratingWhite.visibility = View.VISIBLE
                }
                expandable = !expandable
            }

            placeName.setOnClickListener {
                launchPlaceDescriptionFragment(place.placeId)
            }

        }
    }
}

