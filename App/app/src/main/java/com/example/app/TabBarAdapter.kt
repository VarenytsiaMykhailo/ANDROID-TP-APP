package com.example.app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.app.maps.MapsFragment

class TabBarAdapter(
    activity: FragmentActivity,
    private val tabCount: Int,
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = tabCount

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> HomeFragment.newInstance()
            1 -> ListFragment.newInstance()
            2 -> MapsFragment()
            3 -> ChartFragment.newInstance()
            else -> HomeFragment.newInstance()
        }

}