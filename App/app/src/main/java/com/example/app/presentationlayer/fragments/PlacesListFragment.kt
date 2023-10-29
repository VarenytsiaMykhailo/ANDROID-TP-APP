package com.example.app.presentationlayer.fragments

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
import com.example.app.datalayer.models.Place
import com.example.app.presentationlayer.adapters.PlacesListRecyclerViewAdapter
import com.example.app.presentationlayer.viewmodels.PlacesListFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import okhttp3.internal.notify

class PlacesListFragment : Fragment() {

    private val viewModel by viewModels<PlacesListFragmentViewModel>()

    private val placesListRecyclerViewAdapter = PlacesListRecyclerViewAdapter()

    private lateinit var deletedLocation: Place

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_screen_rv_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.locations_rv).apply {
            layoutManager = GridLayoutManager(context, 1)
            adapter = placesListRecyclerViewAdapter
        }
        viewModel.placesListRecyclerViewAdapter = placesListRecyclerViewAdapter
        val itemTouchHelper = ItemTouchHelper(onMoveCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        viewModel.onUpdatePlaces()
    }

    private var onMoveCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        deletedLocation = viewModel.placesList[position]
                        viewModel.placesList.removeAt(position)
                        placesListRecyclerViewAdapter.notifyItemRemoved(position)

                        view?.let {
                            Snackbar.make(it.findViewById<RecyclerView>(R.id.locations_rv), "${deletedLocation.name} is deleted", Snackbar.LENGTH_LONG).setAction("Undo", View.OnClickListener {
                                viewModel.placesList.add(position,deletedLocation)
                                placesListRecyclerViewAdapter.notifyItemInserted(position)
                            }).show()}

                    }
                }
            }

        }

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