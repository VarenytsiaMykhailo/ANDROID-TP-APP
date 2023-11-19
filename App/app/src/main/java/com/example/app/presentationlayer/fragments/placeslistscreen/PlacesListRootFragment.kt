package com.example.app.presentationlayer.fragments.placeslistscreen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.app.R
import com.example.app.presentationlayer.fragments.placepickermapscreen.PlacePickerMapFragment

/**
 * Use the [PlacesListRootFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlacesListRootFragment : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (childFragmentManager.backStackEntryCount > 0) {
                    childFragmentManager.popBackStack()
                } else {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this, // LifecycleOwner
            onBackPressedCallback
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_places_list_root, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launchPlacesListFragment()
        launchPlacePickerMapFragment()
    }

    private fun launchPlacesListFragment() {
        // Нужно использовать именно child т.к. можно наткнуться на краш при выбивании фрагмента.
        // Но с ним не работает системная кнопка назад (выходит из приложения)
        // Поэтому переопределил колбэк обработки кнопки "назад"
        childFragmentManager
            .beginTransaction()
            .replace(
                R.id.PlacesListRootFragment__FragmentContainerView,
                PlacesListFragment.newInstance()
            ).commit()
    }

    private fun launchPlacePickerMapFragment() {
        // Нужно использовать именно child т.к. можно наткнуться на краш при выбивании фрагмента.
        // Но с ним не работает системная кнопка назад (выходит из приложения)
        // Поэтому переопределил колбэк обработки кнопки "назад"
        childFragmentManager
            .beginTransaction()
            .replace(
                R.id.PlacesListRootFragment__FragmentContainerView,
                PlacePickerMapFragment.newInstance()
            )
            .addToBackStack("PlacePickerMapFragment")
            .commit()
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