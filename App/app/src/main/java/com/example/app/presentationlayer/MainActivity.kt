package com.example.app.presentationlayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.app.businesslayer.providers.MapsAndroidClient
import com.example.app.presentationlayer.adapters.TabBarAdapter
import com.example.app.databinding.ActivityMainBinding
import com.example.app.datalayer.repositories.LocalPropertiesSecretsRepository
import com.google.android.libraries.places.api.Places
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTabBar()
        initMapsAndroidClient()
    }

    private fun setupTabBar() {
        // Disable swipe between fragments
        binding.MainActivityViewPager.isUserInputEnabled = false

        binding.MainActivityViewPager.adapter =
            TabBarAdapter(this, binding.MainActivityTabLayout.tabCount)

        binding.MainActivityViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.MainActivityTabLayout.selectTab(
                        binding.MainActivityTabLayout.getTabAt(position)
                    )
                }
            }
        )

        binding.MainActivityTabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    binding.MainActivityViewPager.setCurrentItem(tab.position, false)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            }
        )
    }

    private fun initMapsAndroidClient() {
        // Initialize Places.
        Places.initialize(applicationContext, LocalPropertiesSecretsRepository.MAPS_API_KEY)
        // Create a new Places client instance.
        val placesClient = Places.createClient(this)
        MapsAndroidClient.placesClient = placesClient
    }
}