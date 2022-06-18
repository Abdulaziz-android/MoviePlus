package com.abdulaziz.movieplus.ui.search

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.abdulaziz.movieplus.R
import com.abdulaziz.movieplus.adapters.SearchAdapter
import com.abdulaziz.movieplus.databinding.FragmentSearchBinding
import com.abdulaziz.movieplus.ui.main.MainActivity
import com.abdulaziz.movieplus.ui.main.MainView
import com.abdulaziz.movieplus.ui.show.ShowFragment
import com.abdulaziz.movieplus.ui.view_states.MovieViewState
import com.abdulaziz.movieplus.utils.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainView?)?.hideBottomBar()
        searchAdapter = SearchAdapter(object : SearchAdapter.OnItemClickListener {
            override fun onItemClicked(movie_id: Int) {
                parentFragmentManager.beginTransaction().replace(
                    R.id.fragment_container, ShowFragment::class.java,
                    bundleOf(Pair("movie_id", movie_id))
                ).addToBackStack("movie").commit()
            }
        })
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var onCreateRunning = true
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var searchViewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)

        searchViewModel = ViewModelProvider(
            this@SearchFragment,
            ViewModelFactory(requireContext())
        )[SearchViewModel::class.java]

        if (!searchViewModel.isObserved()) {
            observeViewModel()
        }

        setUpSearchItem()
        binding.rv.adapter = searchAdapter

        return binding.root
    }

    private fun observeViewModel() {
        GlobalScope.launch(Dispatchers.Main) {
            searchViewModel.state.collect {
                when (it) {
                    is MovieViewState.MovieIdle -> {
                        binding.alertTv.visibility = View.GONE
                    }
                    is MovieViewState.MovieLoadingState -> {
                        binding.alertTv.visibility = View.GONE
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is MovieViewState.MovieLoadedState -> {
                        binding.progressBar.visibility = View.GONE
                        binding.alertTv.visibility = View.GONE
                        searchAdapter.submitList(it.list)
                    }
                    is MovieViewState.MovieNoItemState -> {
                        binding.progressBar.visibility = View.GONE
                        binding.alertTv.visibility = View.VISIBLE
                    }
                    is MovieViewState.MovieErrorState -> {
                        binding.alertTv.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setUpSearchItem() {
        if (onCreateRunning) {
            binding.searchEt.post {
                binding.searchEt.requestFocus()
                val imgr =
                    (activity as MainActivity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imgr.showSoftInput(binding.searchEt, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        binding.searchEt.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == EditorInfo.IME_ACTION_DONE) {
                    searchViewModel.fetchMovies(p0?.text.toString())
                    hideKeyboard()
                    return true
                }
                return false
            }
        })
        onCreateRunning = false
    }


    private fun hideKeyboard() {
        (activity as MainActivity).currentFocus?.let { view ->
            val imm =
                (activity as MainActivity).getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}