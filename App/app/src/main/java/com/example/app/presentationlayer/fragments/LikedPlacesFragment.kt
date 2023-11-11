package com.example.app.presentationlayer.fragments

import android.os.Bundle
import android.util.Log
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
import com.example.app.presentationlayer.viewmodels.LikedPlacesFragmentViewModel
import com.google.android.material.snackbar.Snackbar


class LikedPlacesFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private val viewModel by viewModels<LikedPlacesFragmentViewModel>()

    private val launchPlaceDescriptionFragment: (placeId: String) -> Unit = {
        parentFragmentManager
            .beginTransaction()
            .replace(
                R.id.PlacesListRootFragment__FragmentContainerView,
                PlaceDescriptionFragment.newInstance(it)
            )
            //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack("PlaceDescriptionFragment")
            .commit()
    }

    private val likeCheck: (place: NearbyPlace) ->Boolean = {
        mainActivity.likeCheck(it)
    }

    private val pressLikeButton: (place: NearbyPlace, pressedFlag: Boolean) -> Unit =
        { place: NearbyPlace, pressedFlag: Boolean ->
            if (pressedFlag) {
                viewModel.postSuggestReaction(
                    place.placeId,
                    PlaceReaction.Reaction.LIKE
                )
                mainActivity.deleteLikedPlace(place)
            } else {
                viewModel.postSuggestReaction(
                    place.placeId,
                    PlaceReaction.Reaction.LIKE
                    //TODO поменять на отмену лайка, как появится
                )
                mainActivity.deleteLikedPlace(place)
            }

        }

    private val placesListRecyclerViewAdapter =
        PlacesListRecyclerViewAdapter(launchPlaceDescriptionFragment, pressLikeButton, likeCheck)

    private lateinit var deletedLocation: NearbyPlace

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_places_liked, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = requireActivity() as MainActivity

        viewModel.fragment = this

        val recyclerView = view.findViewById<RecyclerView>(R.id.locations_rv)

        view.findViewById<RecyclerView>(R.id.locations_rv).apply {
            layoutManager = GridLayoutManager(context, 1)
            adapter = placesListRecyclerViewAdapter
        }

        viewModel.placesListRecyclerViewAdapter = placesListRecyclerViewAdapter

        val itemTouchHelper = ItemTouchHelper(onMoveCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)


    }

    override fun onResume() {
        super.onResume()
        viewModel.placesList = mainActivity.getLikedPlace()
        viewModel.onUpdatePlaces()
        placesListRecyclerViewAdapter.notifyDataSetChanged()
    }

    private var onMoveCallback =
        object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT),
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {}

                    ItemTouchHelper.RIGHT -> {
                        deletedLocation = viewModel.placesList[position]
                        viewModel.placesList.removeAt(position)
                        viewModel.onDeletePlace(position)
                        viewModel.postSuggestReaction(
                            deletedLocation.placeId,
                            PlaceReaction.Reaction.VISITED
                        )
                        placesListRecyclerViewAdapter.notifyItemRemoved(position)

                        view?.let {
                            Snackbar.make(
                                it.findViewById<RecyclerView>(R.id.locations_rv),
                                "${deletedLocation.name} посещено",
                                Snackbar.LENGTH_LONG
                            ).setAction("Отменить") {
                                viewModel.placesList.add(position, deletedLocation)
                                viewModel.onRestorePlace(position, deletedLocation)
                                viewModel.postSuggestReaction(
                                    deletedLocation.placeId,
                                    PlaceReaction.Reaction.VISITED
                                    //TODO поменять на отмену VISITED, как появится
                                )
                                placesListRecyclerViewAdapter.notifyItemInserted(position)
                            }.show()
                        }

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
     * @return A new instance of fragment PlacesListFragment.
     */
    companion object {
        @JvmStatic
        fun newInstance(
            //param1: String,
            //param2: String,
        ) = LikedPlacesFragment().apply {
            arguments = Bundle().apply {
                //putString(ARG_PARAM1, param1)
                //putString(ARG_PARAM2, param2)
            }
        }
    }
}
