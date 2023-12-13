package com.example.app.presentationlayer.fragments.onboardingscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.load
import com.example.app.R
import com.example.app.databinding.FragmentOnboardingImageBinding
import com.example.app.datalayer.repositories.LocalPropertiesSecretsRepository
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Use the [OnboardingImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OnboardingImageFragment : Fragment() {

    private lateinit var binding: FragmentOnboardingImageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingImageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageToUse = when (arguments?.getInt(IMAGE_NUMBER_KEY)!!) {
            0 -> {
                R.drawable.onboarding_step1
            }

            1 -> {
                R.drawable.onboarding_step2
            }

            else -> {
                R.color.white
            }
        }

        binding.OnboardingImageFragmentImageView.load(
            "android.resource://" +
                    LocalPropertiesSecretsRepository.APP_PACKAGE_NAME +
                    "/" +
                    imageToUse
        )
    }

    companion object {

        private const val IMAGE_NUMBER_KEY = "IMAGE_NUMBER_KEY"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param imageNumber Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OnboardingImageFragment.
         */
        @JvmStatic
        fun newInstance(
            imageNumber: Int,
            //param2: String,
        ) = OnboardingImageFragment().apply {
            arguments = Bundle().apply {
                //putString(ARG_PARAM1, param1)
                //putString(ARG_PARAM2, param2)
                putInt(IMAGE_NUMBER_KEY, imageNumber)
            }
        }
    }
}