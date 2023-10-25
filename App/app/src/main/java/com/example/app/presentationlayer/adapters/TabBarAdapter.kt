package com.example.app.presentationlayer.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.app.presentationlayer.fragments.MapsFragment
import com.example.app.presentationlayer.fragments.ChartFragment
import com.example.app.presentationlayer.fragments.HomeFragment
import com.example.app.presentationlayer.fragments.PlacesListFragment

class TabBarAdapter(
    activity: FragmentActivity,
    private val tabCount: Int,
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = tabCount

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> HomeFragment.newInstance()
            1 -> PlacesListFragment.newInstance()
            2 -> MapsFragment()
            3 -> ChartFragment.newInstance()
            else -> HomeFragment.newInstance()
        }
}