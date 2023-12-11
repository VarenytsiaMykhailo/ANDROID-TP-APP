package com.example.app.presentationlayer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R

internal class FiltersAdapter(
    private val onAddFilter: (name: String) -> Unit,
    private val onRemoveFilter: (name: String) -> Unit,
    private val onFilterChosen: (name: String) -> Boolean,
) : ListAdapter<String, FiltersAdapter.PlaceViewHolder>(
    LocationDifferentCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder =
        PlaceViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.filter_element, parent, false)
        )

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val name = getItem(position)
        holder.bind(
            name,
            onAddFilter,
            onRemoveFilter,
            onFilterChosen,
        )
    }

    private class LocationDifferentCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String) =
            oldItem == newItem
    }

    internal class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val filterName =
            view.findViewById<TextView>(R.id.FragmentFilters__TextView_Name)

        private val chosenIndicator =
            view.findViewById<ImageView>(R.id.FragmentFilters__ImageView_Indicator)

        private val card =
            view.findViewById<CardView>(R.id.FragmentFilters__CardView_FilterCard)

        fun bind(
            name: String,
            onAddFilter: (name: String) -> Unit,
            onRemoveFilter: (name: String) -> Unit,
            onFilterChosen: (name: String) -> Boolean,
        ) {
            filterName.text = name
            if (onFilterChosen(name)) {
                chosenIndicator.setImageResource(R.drawable.filter_indicator_chosen)
            } else {
                chosenIndicator.setImageResource(R.drawable.filter_indicator_unchosen)
            }
            card.setOnClickListener {
                // It is necessary to check again whether the place exists
                if (!onFilterChosen(name)) {
                    onAddFilter(name)
                    this.chosenIndicator.setImageResource(R.drawable.filter_indicator_chosen)
                } else {
                    onRemoveFilter(name)
                    this.chosenIndicator.setImageResource(R.drawable.filter_indicator_unchosen)
                }
            }

        }
    }
}


