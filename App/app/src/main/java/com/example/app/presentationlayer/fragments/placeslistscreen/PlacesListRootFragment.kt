package com.example.app.presentationlayer.fragments.placeslistscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.app.R

/**
 * Use the [PlacesListRootFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlacesListRootFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_places_list_root, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Нужно использовать именно child т.к. можно наткнуться на краш при выбивании фрагмента
        parentFragmentManager
            .beginTransaction()
            .replace(
                R.id.PlacesListRootFragment__FragmentContainerView,
                PlacesListFragment.newInstance()
            ).commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlacesListRootFragment.
         */
        @JvmStatic
        fun newInstance(
            //param1: String,
            //param2: String,
        ) = PlacesListRootFragment().apply {
            arguments = Bundle().apply {
                //putString(ARG_PARAM1, param1)
                //putString(ARG_PARAM2, param2)
            }
        }
    }
}