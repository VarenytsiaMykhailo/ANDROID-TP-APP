package com.example.app.presentationlayer.fragments.favoriteplaceslistscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.example.app.datalayer.models.NearbyPlace
import com.example.app.datalayer.models.PlaceReaction
import com.example.app.presentationlayer.MainActivity
import com.example.app.presentationlayer.adapters.PlacesListRecyclerViewAdapter
import com.example.app.presentationlayer.fragments.placedescriptionscreen.PlaceDescriptionFragment
import com.example.app.presentationlayer.viewmodels.FavoritePlacesFragmentViewModel
import com.example.app.presentationlayer.viewmodels.FavoritePlacesViewModel
import com.google.android.material.snackbar.Snackbar


/**
 * Use the [FavoritePlacesListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoritePlacesListFragment : Fragment() {

    private val viewModel by viewModels<FavoritePlacesFragmentViewModel>()

    private val favoritePlacesViewModel by viewModels<FavoritePlacesViewModel>()

    private lateinit var mainActivity: MainActivity

    private val onLaunchPlaceDescriptionFragment: (placeId: String) -> Unit = {
        parentFragmentManager
            .beginTransaction()
            .replace(
                R.id.FavoritePlacesListRootFragment__FragmentContainerView,
                PlaceDescriptionFragment.newInstance(it)
            )
            //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack("PlaceDescriptionFragment")
            .commit()
    }

    private val onRemovePlaceFromFavorite: (place: NearbyPlace) -> Unit = {
        favoritePlacesViewModel.removePlace(it)
        viewModel.favoritePlacesList.removeIf { placeFromList ->
            placeFromList.placeId == it.placeId
        }
        placesListRecyclerViewAdapter.notifyDataSetChanged()
    }

    private val onPlaceExistsInFavorite: (place: NearbyPlace) -> Boolean = {
        favoritePlacesViewModel.placeExists(it)
    }

    private val placesListRecyclerViewAdapter =
        PlacesListRecyclerViewAdapter(
            onLaunchPlaceDescriptionFragment,
            null,
            onRemovePlaceFromFavorite,
            onPlaceExistsInFavorite
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_favorite_places_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = requireActivity() as MainActivity

        viewModel.fragment = this

        setupRecyclerView(view)
    }

    override fun onResume() {
        super.onResume()

        viewModel.onUpdatePlaces()
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.locations_rv)
        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 1)
            adapter = placesListRecyclerViewAdapter
        }

        ItemTouchHelper(onMoveCallback).attachToRecyclerView(recyclerView)
        viewModel.placesListRecyclerViewAdapter = placesListRecyclerViewAdapter
    }

    private val onMoveCallback =
        object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT,
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean =
                false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        val placeToUnLike = viewModel.favoritePlacesList[position]
                        favoritePlacesViewModel.removePlace(placeToUnLike)
                        viewModel.onUpdatePlaces()
                        viewModel.postSuggestReaction(
                            placeToUnLike.placeId,
                            PlaceReaction.Reaction.UNLIKE
                        )

                        view?.let {
                            Snackbar.make(
                                it.findViewById<RecyclerView>(R.id.locations_rv),
                                "${placeToUnLike.name} убрано из избранного",
                                Snackbar.LENGTH_LONG
                            ).setAction("Отменить") {
                                viewModel.postSuggestReaction(
                                    placeToUnLike.placeId,
                                    PlaceReaction.Reaction.LIKE
                                )

                                favoritePlacesViewModel.savePlace(placeToUnLike)
                                viewModel.onUpdatePlaces()
                            }.show()
                        }

                    }

                    ItemTouchHelper.RIGHT -> {}
                }
            }
        }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritePlacesListFragment.
     */
    companion object {
        @JvmStatic
        fun newInstance(
            //param1: String,
            //param2: String,
        ) = FavoritePlacesListFragment().apply {
            arguments = Bundle().apply {
                //putString(ARG_PARAM1, param1)
                //putString(ARG_PARAM2, param2)
            }
        }
    }
}
