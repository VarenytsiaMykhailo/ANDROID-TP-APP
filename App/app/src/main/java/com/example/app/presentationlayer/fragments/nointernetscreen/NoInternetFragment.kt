package com.example.app.presentationlayer.fragments.nointernetscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.app.databinding.FragmentNoInternetBinding
import com.example.app.domain.providers.MapProvider
import kotlinx.coroutines.runBlocking

/**
 * Use the [NoInternetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NoInternetFragment : Fragment() {

    private lateinit var binding: FragmentNoInternetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoInternetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.NoInternetFragmentButtonRefresh.setOnClickListener {
            runBlocking {
                if (MapProvider.getPing()) {
                    closeScreen()
                }
            }
        }
    }

    private fun closeScreen() {
        parentFragmentManager.popBackStack()
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoInternetFragment.
     */
    companion object {
        @JvmStatic
        fun newInstance(
            //param1: String,
            //param2: String,
        ) = NoInternetFragment().apply {
            arguments = Bundle().apply {
                //putString(ARG_PARAM1, param1)
                //putString(ARG_PARAM2, param2)
            }
        }
    }
}