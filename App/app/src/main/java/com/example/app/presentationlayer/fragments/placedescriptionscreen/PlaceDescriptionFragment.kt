package com.example.app.presentationlayer.fragments.placedescriptionscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.app.databinding.FragmentPlaceDescriptionBinding
import com.example.app.presentationlayer.adapters.PlaceDescriptionImagesSliderRecyclerViewAdapter
import com.example.app.presentationlayer.viewmodels.PlaceDescriptionFragmentViewModel

/**
 * Use the [PlaceDescriptionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlaceDescriptionFragment : Fragment() {

    private lateinit var binding: FragmentPlaceDescriptionBinding

    private val viewModel by viewModels<PlaceDescriptionFragmentViewModel>()

    private val placeDescriptionImagesSliderRecyclerViewAdapter =
        PlaceDescriptionImagesSliderRecyclerViewAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaceDescriptionBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fragment = this

        binding.PlaceDescriptionFragmentViewPager2PlaceImage.adapter =
            placeDescriptionImagesSliderRecyclerViewAdapter

        viewModel.placeDescriptionImagesSliderRecyclerViewAdapter =
            placeDescriptionImagesSliderRecyclerViewAdapter

        val placeId: String = arguments?.getString(PLACE_ID_KEY)!!
        //viewModel.onSetContent(placeId)
        viewModel.onSetContent("ChIJfRJDflpKtUYRl0UbgcrmUUk")

        binding.PlaceDescriptionFragmentImageViewBackButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    fun onSetTitle(title: String) {
        binding.PlaceDescriptionFragmentTextViewTitle.text = title
    }

    fun onSetDescription(description: String) {
        binding.PlaceDescriptionFragmentTextViewDescription.setIsExpanded(false)
        binding.PlaceDescriptionFragmentTextViewDescription.text = description
    }

    fun onSetRating(rating: Double) {
        binding.PlaceDescriptionFragmentTextViewRating.text = rating.toString()
        binding.PlaceDescriptionFragmentRatingBar.rating = rating.toFloat()
    }

    fun onSetRatingCount(ratingCount: Int) {
        binding.PlaceDescriptionFragmentTextViewRatingCount.text = "$ratingCount оценок"
    }

    fun onSetAddress(address: String) {
        binding.PlaceDescriptionFragmentTextViewAddress.text = address
    }

    fun onSetWorkingHours(workingHours: String) {
        binding.PlaceDescriptionFragmentTextViewWorkingHours.text = workingHours
    }

    companion object {

        const val PLACE_ID_KEY = "PLACE_ID_KEY"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param placeId Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlaceDescriptionFragment.
         */
        @JvmStatic
        fun newInstance(
            placeId: String,
            //param2: String,
        ) = PlaceDescriptionFragment().apply {
            arguments = Bundle().apply {
                putString(PLACE_ID_KEY, placeId)
                //putString(ARG_PARAM2, param2)
            }
        }
    }
}