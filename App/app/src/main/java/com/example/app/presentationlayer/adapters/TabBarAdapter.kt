package com.example.app.presentationlayer.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.app.presentationlayer.fragments.mapscreen.MapFragment
import com.example.app.presentationlayer.fragments.ChartFragment
import com.example.app.presentationlayer.fragments.placedescriptionscreen.PlaceDescriptionFragment
import com.example.app.presentationlayer.fragments.placeslistscreen.PlacesListRootFragment

class TabBarAdapter(
    activity: FragmentActivity,
    private val tabCount: Int,
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = tabCount

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> PlacesListRootFragment.newInstance()
            1 -> MapFragment.newInstance()
            2 -> PlaceDescriptionFragment.newInstance("ChIJfRJDflpKtUYRl0UbgcrmUUk")
            3 -> ChartFragment.newInstance()
            else -> ChartFragment.newInstance()
        }
}