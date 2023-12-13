package com.example.app.presentationlayer.fragments.onboardingscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.app.databinding.FragmentOnboardingRootBinding
import com.example.app.presentationlayer.adapters.OnboardingViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Use the [OnboardingRootFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OnboardingRootFragment: Fragment() {

    private lateinit var binding: FragmentOnboardingRootBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingRootBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager2()
        TabLayoutMediator(
            binding.OnboardingRootFragmentTabLayout,
            binding.OnboardingRootFragmentViewPager
        ) { tab, position ->
        }.attach()
    }

    private fun setupViewPager2() {
        binding.OnboardingRootFragmentViewPager.adapter =
            OnboardingViewPagerAdapter(requireActivity(), 3)

        binding.OnboardingRootFragmentViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (position == 2) {
                        closeScreen()
                    }
                }
            }
        )

        binding.OnboardingRootFragmentViewPager.currentItem = 0
    }

    private fun closeScreen() {
        parentFragmentManager.popBackStack()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OnboardingRootFragment.
         */
        @JvmStatic
        fun newInstance(
            //param1: String,
            //param2: String,
        ) = OnboardingRootFragment().apply {
            arguments = Bundle().apply {
                //putString(ARG_PARAM1, param1)
                //putString(ARG_PARAM2, param2)
            }
        }
    }
}