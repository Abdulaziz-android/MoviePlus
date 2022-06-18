package com.abdulaziz.movieplus.ui.saved

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.abdulaziz.movieplus.R
import com.abdulaziz.movieplus.adapters.SavedAdapter
import com.abdulaziz.movieplus.data.local.AppDatabase
import com.abdulaziz.movieplus.data.models.movie.Movie
import com.abdulaziz.movieplus.databinding.FragmentSavedBinding
import com.abdulaziz.movieplus.ui.main.MainView
import com.abdulaziz.movieplus.ui.show.ShowFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SavedFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedAdapter = SavedAdapter(object : SavedAdapter.OnItemClickListener{
            override fun onItemClicked(movie_id: Int) {
                parentFragmentManager.beginTransaction().replace(
                    R.id.fragment_container, ShowFragment::class.java,
                    bundleOf(Pair("movie_id", movie_id))
                ).addToBackStack("movie").commit()
            }

            override fun onSaveClicked(movie: Movie) {
                removeFromSaved(movie)
            }
        })
    }

    private var _binding: FragmentSavedBinding? =null
    private val binding get() = _binding!!
    private lateinit var savedAdapter: SavedAdapter
    private lateinit var database:AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(layoutInflater, container, false)
        (activity as MainView?)?.showBottomBar()
        database = AppDatabase.getInstance(requireContext())
        binding.rv.adapter = savedAdapter
        loadData()
        return binding.root
    }

    private fun loadData() {
        GlobalScope.launch(Dispatchers.Main) {
            val savedMovies = database.movieDao().getSavedMovies()
            if (savedMovies.isNotEmpty()){
                savedAdapter.submitList(savedMovies)
            }else {
                savedAdapter.submitList(arrayListOf())
            }
        }
    }

    private fun removeFromSaved(movie: Movie){
        GlobalScope.launch {
            movie.isSaved = false
            database.movieDao().insert(movie)
        }
    }

}