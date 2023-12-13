package com.example.app.presentationlayer.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.app.presentationlayer.fragments.onboardingscreen.OnboardingImageFragment

class OnboardingViewPagerAdapter(
    activity: FragmentActivity,
    private val tabCount: Int,
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = tabCount

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> OnboardingImageFragment.newInstance(position)
            1 -> OnboardingImageFragment.newInstance(position)
            else -> OnboardingImageFragment.newInstance(position)
        }
}