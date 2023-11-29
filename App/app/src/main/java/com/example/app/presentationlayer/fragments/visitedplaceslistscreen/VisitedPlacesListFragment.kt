package com.example.app.presentationlayer.fragments.visitedplaceslistscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.example.app.datalayer.models.NearbyPlace
import com.example.app.presentationlayer.MainActivity
import com.example.app.presentationlayer.adapters.PlacesListRecyclerViewAdapter
import com.example.app.presentationlayer.fragments.placedescriptionscreen.PlaceDescriptionFragment
import com.example.app.presentationlayer.viewmodels.FavoritePlacesViewModel
import com.example.app.presentationlayer.viewmodels.VisitedPlacesFragmentViewModel

/**
 * Use the [VisitedPlacesListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VisitedPlacesListFragment : Fragment() {

    private val viewModel by viewModels<VisitedPlacesFragmentViewModel>()

    private val favoritePlacesViewModel by viewModels<FavoritePlacesViewModel>()

    private lateinit var mainActivity: MainActivity

    private val onLaunchPlaceDescriptionFragment: (placeId: String) -> Unit = {
        parentFragmentManager
            .beginTransaction()
            .replace(
                R.id.VisitedPlacesListRootFragment__FragmentContainerView,
                PlaceDescriptionFragment.newInstance(it)
            )
            //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack("PlaceDescriptionFragment")
            .commit()
    }

    private val onAddPlaceToFavorite: (place: NearbyPlace) -> Unit = {
        favoritePlacesViewModel.savePlace(it)
    }

    private val onRemovePlaceFromFavorite: (place: NearbyPlace) -> Unit = {
        favoritePlacesViewModel.removePlace(it)
    }

    private val onPlaceExistsInFavorite: (place: NearbyPlace) -> Boolean = {
        favoritePlacesViewModel.placeExists(it)
    }

    private val placesListRecyclerViewAdapter =
        PlacesListRecyclerViewAdapter(
            onLaunchPlaceDescriptionFragment,
            onAddPlaceToFavorite,
            onRemovePlaceFromFavorite,
            onPlaceExistsInFavorite
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_visited_places_list, container, false)

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

        //ItemTouchHelper(onMoveCallback).attachToRecyclerView(recyclerView)
        viewModel.placesListRecyclerViewAdapter = placesListRecyclerViewAdapter
    }

    private val onMoveCallback =
        object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.RIGHT,
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
                    ItemTouchHelper.LEFT -> {}

                    ItemTouchHelper.RIGHT -> {
                        /*
                        val placeToVisited = viewModel.favoritePlacesList[position]
                        val indexOfCachedListForRestoring =
                            viewModel.onVisitedPlace(position, placeToVisited)
                            favoritePlacesViewModel.removePlace(placeToVisited)

                        view?.let {
                            Snackbar.make(
                                it.findViewById<RecyclerView>(R.id.locations_rv),
                                "${placeToVisited.name} посещено",
                                Snackbar.LENGTH_LONG
                            ).setAction("Отменить") {
                                viewModel.onRestoreVisitedPlace(
                                    position,
                                    indexOfCachedListForRestoring,
                                    placeToVisited,
                                )
                                favoritePlacesViewModel.savePlace(placeToVisited)
                            }.show()
                        }

                         */
                    }
                }
            }
        }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VisitedPlacesListFragment.
     */
    companion object {
        @JvmStatic
        fun newInstance(
            //param1: String,
            //param2: String,
        ) = VisitedPlacesListFragment().apply {
            arguments = Bundle().apply {
                //putString(ARG_PARAM1, param1)
                //putString(ARG_PARAM2, param2)
            }
        }
    }
}