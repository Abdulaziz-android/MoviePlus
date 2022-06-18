package com.abdulaziz.movieplus.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abdulaziz.movieplus.R
import com.abdulaziz.movieplus.databinding.FragmentProfileBinding
import com.abdulaziz.movieplus.ui.main.MainView

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? =null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        (activity as MainView?)?.showBottomBar()
        return binding.root
    }


}