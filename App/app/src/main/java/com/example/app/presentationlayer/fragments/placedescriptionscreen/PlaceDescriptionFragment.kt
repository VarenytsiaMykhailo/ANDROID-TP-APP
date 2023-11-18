package com.example.app.presentationlayer.fragments.placedescriptionscreen

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.app.R
import com.example.app.databinding.FragmentPlaceDescriptionBinding
import com.example.app.datalayer.models.PlaceDescription
import com.example.app.presentationlayer.adapters.PlaceDescriptionImagesSliderRecyclerViewAdapter
import com.example.app.presentationlayer.viewmodels.FavoritePlacesViewModel
import com.example.app.presentationlayer.viewmodels.PlaceDescriptionFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay

/**
 * Use the [PlaceDescriptionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlaceDescriptionFragment : Fragment() {

    private lateinit var binding: FragmentPlaceDescriptionBinding

    private val viewModel by viewModels<PlaceDescriptionFragmentViewModel>()
    private val favoritePlacesViewModel by viewModels<FavoritePlacesViewModel>()

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
        viewModel.onSetContent(placeId)

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

    /*
   fun onSetMap(staticMapImageUrl: String, googleMapAppDeeplink: String) {
       binding.PlaceDescriptionFragmentImageViewStaticMap.apply {
           load(staticMapImageUrl)
           setOnClickListener {
               launchGoogleMapApp(googleMapAppDeeplink)
           }
       }
    }
    */

    fun onSetMap(nearbyPlace: PlaceDescription) {
        childFragmentManager.beginTransaction()
            .replace(
                R.id.PlaceDescriptionFragment__FragmentContainerView_SmallMap,
                SmallMapFragment.newInstance(
                    nearbyPlace.location.lat.toString(),
                    nearbyPlace.location.lng.toString(),
                )
            )
            .commit()
    }

    private fun launchGoogleMapApp(requestUrl: String) {
        val intentDeeplink = Uri.parse(requestUrl)
        val intent = Intent(Intent.ACTION_VIEW, intentDeeplink)
        intent.setPackage("com.google.android.apps.maps")
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e(LOG_TAG, "outer catch e = $e")
            try {
                val unrestrictedIntent = Intent(Intent.ACTION_VIEW, intentDeeplink)
                startActivity(unrestrictedIntent)
            } catch (e: ActivityNotFoundException) {
                Log.e(LOG_TAG, "inner catch e = $e")
                Snackbar.make(
                    binding.PlaceDescriptionFragmentFragmentContainerViewSmallMap,
                    "Установите приложение Google Maps",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun onSetAddress(address: String) {
        binding.PlaceDescriptionFragmentTextViewAddress.text = address
    }

    fun onSetWorkingHours(workingHours: String) {
        binding.PlaceDescriptionFragmentTextViewWorkingHours.text = workingHours
    }

    fun onSetLike() {
        binding.DescriptionFragmentImageViewLike.setImageResource(R.drawable.like_liked)
    }

    fun onUnSetLike() {
        binding.DescriptionFragmentImageViewLike.setImageResource(R.drawable.like_unliked)
    }

    fun onSetVisited() {
        binding.DescriptionFragmentImageViewVisit.setImageResource(R.drawable.visited_icon)
    }

    fun onUnSetVisited() {
        binding.DescriptionFragmentImageViewVisit.setImageResource(R.drawable.unvisited_icon)
    }

    suspend fun showTag1(text: String) {
        val textView = binding.PlacesListFragmentTextViewTag1
        val imageViewRoot= binding.PlaceDescriptionFragmentCardViewTag1Root
        val imageView= binding.PlaceDescriptionFragmentImageViewTag1
        imageViewRoot.visibility=View.VISIBLE
        textView.visibility=View.VISIBLE
        textView.text=text
        delay(1000)
        //ИСПРАВИТЬ
        Log.d("sss","${textView.width}")
        imageView.layoutParams.width = textView.width+8
    }
    suspend fun showTag2(text: String) {
        val textView = binding.PlacesListFragmentTextViewTag2
        val imageViewRoot= binding.PlaceDescriptionFragmentCardViewTag2Root
        val imageView= binding.PlaceDescriptionFragmentImageViewTag2
        imageViewRoot.visibility=View.VISIBLE
        textView.visibility=View.VISIBLE
        textView.text=text
        Log.d("sss","${textView.width}")
        imageView.layoutParams.width = textView.width+8
    }

    suspend fun showTag3(text: String) {
        val textView = binding.PlacesListFragmentTextViewTag3
        val imageViewRoot= binding.PlaceDescriptionFragmentCardViewTag3Root
        val imageView= binding.PlaceDescriptionFragmentImageViewTag3
        imageViewRoot.visibility=View.VISIBLE
        textView.visibility=View.VISIBLE
        textView.text=text
        Log.d("sss","${textView.width}")
        imageView.layoutParams.width = textView.width+8
    }


    companion object {

        private const val LOG_TAG = "PlaceDescriptionFragment"

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