package com.example.app.presentationlayer.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.app.presentationlayer.ui.ChartFragment
import com.example.app.presentationlayer.ui.HomeFragment
import com.example.app.presentationlayer.ui.LocationRvFragment

class TabBarAdapter(
    activity: FragmentActivity,
    private val tabCount: Int,
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = tabCount

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> HomeFragment.newInstance()
            1 -> LocationRvFragment.newInstance()
            2 -> ChartFragment.newInstance()
            else -> HomeFragment.newInstance()
        }

}