package com.example.app.presentationlayer.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.app.presentationlayer.fragments.favoriteplaceslistscreen.FavoritePlacesListRootFragment
import com.example.app.presentationlayer.fragments.placeslistscreen.PlacesListRootFragment
import com.example.app.presentationlayer.fragments.visitedplaceslistscreen.VisitedPlacesListRootFragment

class TabBarAdapter(
    activity: FragmentActivity,
    private val tabCount: Int,
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = tabCount

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> PlacesListRootFragment.newInstance()
            1 -> FavoritePlacesListRootFragment.newInstance()
            2 -> VisitedPlacesListRootFragment.newInstance()
            else -> PlacesListRootFragment.newInstance()
        }
}