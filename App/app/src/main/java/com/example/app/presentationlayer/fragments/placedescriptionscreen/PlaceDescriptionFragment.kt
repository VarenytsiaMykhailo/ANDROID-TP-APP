package com.example.app.presentationlayer.fragments.placedescriptionscreen

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.app.R
import com.example.app.databinding.FragmentPlaceDescriptionBinding
import com.example.app.datalayer.models.PlaceDescription
import com.example.app.datalayer.models.PlaceReaction
import com.example.app.domain.providers.toNearbyPlace
import com.example.app.presentationlayer.adapters.PlaceDescriptionImagesSliderRecyclerViewAdapter
import com.example.app.presentationlayer.viewmodels.FavoritePlacesViewModel
import com.example.app.presentationlayer.viewmodels.PlaceDescriptionFragmentViewModel
import com.example.app.presentationlayer.viewmodels.VisitedPlacesViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


/**
 * Use the [PlaceDescriptionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlaceDescriptionFragment : Fragment() {

    private lateinit var binding: FragmentPlaceDescriptionBinding

    lateinit var place: PlaceDescription

    private val viewModel by viewModels<PlaceDescriptionFragmentViewModel>()

    private val favoritePlacesViewModel by viewModels<FavoritePlacesViewModel>()

    private val visitedPlacesViewModel by viewModels<VisitedPlacesViewModel>()

    private val placeDescriptionImagesSliderRecyclerViewAdapter =
        PlaceDescriptionImagesSliderRecyclerViewAdapter()

    private var likedFlag = false
    private var visitedFlag = false
    var placeID: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaceDescriptionBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fragment = this

        binding.PlaceDescriptionFragmentViewPager2PlaceImage.adapter =
            placeDescriptionImagesSliderRecyclerViewAdapter

        placeID = arguments?.getString(PLACE_ID_KEY)!!

        showTags()

        TabLayoutMediator(
            binding.PlaceDescriptionFragmentTabLayout,
            binding.PlaceDescriptionFragmentViewPager2PlaceImage
        ) { tab, position ->
        }.attach()

        binding.PlaceDescriptionFragmentImageViewBackButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.DescriptionFragmentImageViewLike.setOnClickListener {
            if (likedFlag) {
                likedFlag = false
                onUnSetLike()
                favoritePlacesViewModel.removePlace(place.toNearbyPlace())
            } else {
                likedFlag = true
                onSetLike()
                favoritePlacesViewModel.savePlace(place.toNearbyPlace())
            }
        }
        binding.DescriptionFragmentImageViewVisit.setOnClickListener {
            if (visitedFlag) {
                visitedFlag = false
                onUnSetVisited()
                visitedPlacesViewModel.removePlace(place.toNearbyPlace())
                //viewModel.postReaction(place.placeId, PlaceReaction.Reaction.UNVISITED)
            } else {
                visitedFlag = true
                onSetVisited()
                visitedPlacesViewModel.savePlace(place.toNearbyPlace())
                //viewModel.postReaction(place.placeId, PlaceReaction.Reaction.VISITED)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.place.onEach { placeDescription ->
                onSetTitle(placeDescription.name)
                placeDescriptionImagesSliderRecyclerViewAdapter.submitList(placeDescription.photos)
                onSetDescription(placeDescription.description)
                onSetRating(placeDescription.rating)
                onSetRatingCount(placeDescription.ratingCount)
                onSetMap(placeDescription)
                onSetAddress(placeDescription.address)
                onSetWorkingHours(placeDescription.workingHours)
                onSetTags(placeDescription.tags)
                onSetStartReactions(placeDescription.reactions)
                place = placeDescription
            }.collect()
        }


    }

    fun disableScroll() {
        binding.PlaceDescriptionFragmentScrollView.requestDisallowInterceptTouchEvent(true)
    }

    private fun onSetTitle(title: String) {
        binding.PlaceDescriptionFragmentTextViewTitle.text = title
    }

    private fun onSetDescription(description: String) {
        binding.PlaceDescriptionFragmentTextViewDescription.setIsExpanded(false)
        binding.PlaceDescriptionFragmentTextViewDescription.text = description
    }

    private fun onSetRating(rating: Double) {
        binding.PlaceDescriptionFragmentTextViewRating.text = rating.toString()
        binding.PlaceDescriptionFragmentRatingBar.rating = rating.toFloat()
    }

    private fun onSetRatingCount(ratingCount: Int) {
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
    private fun onSetTags(tagsList: List<String>) {
        if (tagsList.isNotEmpty()) {
            setTag1(tagsList[0])
            if (tagsList.size >= 2) {
                setTag2(tagsList[1])
                if (tagsList.size >= 3) {
                    setTag3(tagsList[2])
                }
            }
        }
    }

    private fun onSetMap(nearbyPlace: PlaceDescription) {
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

    private fun onSetAddress(address: String) {
        binding.PlaceDescriptionFragmentTextViewAddress.text = address
    }

    private fun onSetWorkingHours(workingHours: List<String>) {
        val workingHoursString = workingHours.joinToString("\n")
        binding.PlaceDescriptionFragmentTextViewWorkingHours.text = workingHoursString
    }

    private fun onSetLike() {
        binding.DescriptionFragmentImageViewLike.setImageResource(R.drawable.like_liked)
    }

    private fun onUnSetLike() {
        binding.DescriptionFragmentImageViewLike.setImageResource(R.drawable.like_unliked)
    }

    private fun onSetVisited() {
        binding.DescriptionFragmentImageViewVisit.setImageResource(R.drawable.visited_icon)
        binding.PlaceDescriptionFragmentTextViewUnvisited.visibility = View.GONE
    }

    private fun onUnSetVisited() {
        binding.DescriptionFragmentImageViewVisit.setImageResource(R.drawable.unvisited_icon)
        binding.PlaceDescriptionFragmentTextViewUnvisited.visibility = View.VISIBLE
    }

    private fun onSetStartReactions(list: List<String>) {
        if (list.isNotEmpty())
            list.forEach { it ->
                String
                likedFlag = if (it == "like") {
                    onSetLike()
                    true
                } else {
                    onUnSetLike()
                    false
                }

                visitedFlag = if (it == "visited") {
                    onSetVisited()
                    true
                } else {
                    onUnSetVisited()
                    false
                }
            }
        else {
            onUnSetLike()
            likedFlag = false
            onUnSetVisited()
            visitedFlag = false
        }
    }

    private fun Int.toPx() = (this * resources.displayMetrics.density).toInt()

    private fun setTag1(text: String) {
        val textView = binding.PlaceDescriptionFragmentTextViewTag1
        val imageView = binding.PlaceDescriptionFragmentImageViewTag1
        textView.text = text
        imageView.layoutParams.width = (text.length * 12 + 8).toPx()
        imageView.requestLayout()
    }

    private fun setTag2(text: String) {
        val textView = binding.PlaceDescriptionFragmentTextViewTag2
        val imageView = binding.PlaceDescriptionFragmentImageViewTag2
        textView.text = text
        imageView.layoutParams.width = (text.length * 12 + 8).toPx()
        imageView.requestLayout()
    }

    private fun setTag3(text: String) {
        val textView = binding.PlaceDescriptionFragmentTextViewTag3
        val imageView = binding.PlaceDescriptionFragmentImageViewTag3
        textView.text = text
        imageView.layoutParams.width = (text.length * 12 + 8).toPx()
        imageView.requestLayout()
    }

    private fun showTags() {
        binding.PlaceDescriptionFragmentTextViewTag1.visibility = View.VISIBLE
        binding.PlaceDescriptionFragmentCardViewTag1Root.visibility = View.VISIBLE
        binding.PlaceDescriptionFragmentTextViewTag2.visibility = View.VISIBLE
        binding.PlaceDescriptionFragmentCardViewTag2Root.visibility = View.VISIBLE
        binding.PlaceDescriptionFragmentTextViewTag3.visibility = View.VISIBLE
        binding.PlaceDescriptionFragmentCardViewTag3Root.visibility = View.VISIBLE
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