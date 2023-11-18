package com.example.app.presentationlayer.fragments.placeslistscreen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.example.app.datalayer.models.NearbyPlace
import com.example.app.presentationlayer.MainActivity
import com.example.app.presentationlayer.adapters.PlacesListRecyclerViewAdapter
import com.example.app.presentationlayer.fragments.mapscreen.MapFragment
import com.example.app.presentationlayer.fragments.placedescriptionscreen.PlaceDescriptionFragment
import com.example.app.presentationlayer.viewmodels.FavoritePlacesViewModel
import com.example.app.presentationlayer.viewmodels.PlacesListFragmentViewModel
import com.google.android.material.snackbar.Snackbar

/**
 * Use the [PlacesListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlacesListFragment : Fragment() {

    private val viewModel by viewModels<PlacesListFragmentViewModel>()

    private val favoritePlacesViewModel by viewModels<FavoritePlacesViewModel>()

    internal lateinit var mainActivity: MainActivity

    private val onLaunchPlaceDescriptionFragment: (placeId: String) -> Unit = {
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
        inflater.inflate(R.layout.fragment_places_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = requireActivity() as MainActivity
        mainActivity.onLocationPermissionGrantedForPlacesListFragment = viewModel::onUpdatePlaces

        viewModel.fragment = this

        setupRecyclerView(view)

        setChangeFragmentButtonClickListener(view)
    }

    override fun onResume() {
        super.onResume()
        // TODO здесь пока нужен true чтобы учтелся свайп с экрана рекомендации
        // TODO но это не помогает снять иконку лайка
        viewModel.onUpdatePlaces(false)
        Log.d("qwerty123", "PlacesListFragment onResume")
        // TODO placesListRecyclerViewAdapter.notifyDataSetChanged() но если добавить это, то лайк ремувнется
        // TODO и тогда в viewModel.onUpdatePlaces() true можно не передавать
        placesListRecyclerViewAdapter.notifyDataSetChanged()
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

    private fun setChangeFragmentButtonClickListener(view: View) {
        // TODO переделать чтобы можно было свитчиться между фрагментами с сохранением состояния
        view.findViewById<Button>(R.id.change_fragment_button).setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.PlacesListRootFragment__FragmentContainerView,
                    MapFragment.newInstance()
                )
                //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("MapFragment")
                .commit()
        }
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
            ): Boolean =
                false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        val placeToDelete = viewModel.placesList[position]
                        viewModel.onRemovePlace(position, placeToDelete)
                        val placeExistsInFavoriteDb = favoritePlacesViewModel.placeExists(placeToDelete)
                        favoritePlacesViewModel.removePlace(placeToDelete)

                        view?.let {
                            Snackbar.make(
                                it.findViewById<RecyclerView>(R.id.locations_rv),
                                "${placeToDelete.name} удалено",
                                Snackbar.LENGTH_LONG
                            ).setAction("Отменить") {
                                viewModel.onRestoreRemovedPlace(position, placeToDelete)
                                if (placeExistsInFavoriteDb) {
                                    favoritePlacesViewModel.savePlace(placeToDelete)
                                }
                            }.show()
                        }
                    }

                    ItemTouchHelper.RIGHT -> {
                        val placeToVisited = viewModel.placesList[position]
                        viewModel.onVisitedPlace(position, placeToVisited)

                        view?.let {
                            Snackbar.make(
                                it.findViewById<RecyclerView>(R.id.locations_rv),
                                "${placeToVisited.name} посещено",
                                Snackbar.LENGTH_LONG
                            ).setAction("Отменить") {
                                viewModel.onRestoreVisitedPlace(position, placeToVisited)
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
        ) = PlacesListFragment().apply {
            arguments = Bundle().apply {
                //putString(ARG_PARAM1, param1)
                //putString(ARG_PARAM2, param2)
            }
        }
    }
}
