package com.example.app.presentationlayer.fragments.placeslistscreen

import android.os.Bundle
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
import com.example.app.datalayer.models.Place
import com.example.app.presentationlayer.adapters.PlacesListRecyclerViewAdapter
import com.example.app.presentationlayer.fragments.placedescriptionscreen.PlaceDescriptionFragment
import com.example.app.presentationlayer.viewmodels.PlacesListFragmentViewModel
import com.google.android.material.snackbar.Snackbar

/**
 * Use the [PlacesListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlacesListFragment : Fragment() {

    private val viewModel by viewModels<PlacesListFragmentViewModel>()

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

    private val placesListRecyclerViewAdapter =
        PlacesListRecyclerViewAdapter(launchPlaceDescriptionFragment)

    private lateinit var deletedLocation: Place

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_places_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.locations_rv)

        view.findViewById<RecyclerView>(R.id.locations_rv).apply {
            layoutManager = GridLayoutManager(context, 1)
            adapter = placesListRecyclerViewAdapter
        }

        viewModel.placesListRecyclerViewAdapter = placesListRecyclerViewAdapter

        val itemTouchHelper = ItemTouchHelper(onMoveCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        viewModel.onUpdatePlaces()
        view.findViewById<Button>(R.id.change_fragment_button).setOnClickListener {

            //Миша сделай

        }

    }

    private var onMoveCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)) {
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
                    ItemTouchHelper.LEFT -> {
                        deletedLocation = viewModel.placesList[position]
                        viewModel.placesList.removeAt(position)
                        placesListRecyclerViewAdapter.notifyItemRemoved(position)

                        view?.let {
                            Snackbar.make(
                                it.findViewById<RecyclerView>(R.id.locations_rv),
                                "${deletedLocation.name} удалено",
                                Snackbar.LENGTH_LONG
                            ).setAction("Отменить", View.OnClickListener {
                                viewModel.placesList.add(position, deletedLocation)
                                placesListRecyclerViewAdapter.notifyItemInserted(position)
                            }).show()
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
