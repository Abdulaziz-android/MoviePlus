package com.abdulaziz.movieplus.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.abdulaziz.movieplus.R
import com.abdulaziz.movieplus.adapters.RecentAdapter
import com.abdulaziz.movieplus.adapters.ViewPagerAdapter
import com.abdulaziz.movieplus.data.local.AppDatabase
import com.abdulaziz.movieplus.databinding.FragmentBaseBinding
import com.abdulaziz.movieplus.repositories.MovieIntent
import com.abdulaziz.movieplus.transformer.CardTransformer
import com.abdulaziz.movieplus.transformer.HorizontalMarginItemDecoration
import com.abdulaziz.movieplus.ui.main.MainView
import com.abdulaziz.movieplus.ui.search.SearchFragment
import com.abdulaziz.movieplus.ui.show.ShowFragment
import com.abdulaziz.movieplus.ui.view_states.MovieViewState
import com.abdulaziz.movieplus.utils.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BaseFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pagerAdapter = ViewPagerAdapter(object : ViewPagerAdapter.OnPageClickListener {
            override fun onPageClick(movie_id: Int) {
                parentFragmentManager.beginTransaction().replace(
                    R.id.fragment_container, ShowFragment::class.java,
                    bundleOf(Pair("movie_id", movie_id))
                ).addToBackStack("movie").commit()
            }
        })
        recentAdapter = RecentAdapter(object : RecentAdapter.OnItemClickListener {
            override fun onItemClicked(movie_id: Int) {
                parentFragmentManager.beginTransaction().replace(
                    R.id.fragment_container, ShowFragment::class.java,
                    bundleOf(Pair("movie_id", movie_id))
                ).addToBackStack("movie").commit()
            }
        })
    }

    private var _binding: FragmentBaseBinding? = null
    private val binding get() = _binding!!
    private lateinit var pagerAdapter: ViewPagerAdapter
    private lateinit var recentAdapter: RecentAdapter
    private lateinit var baseViewModel: BaseViewModel
    private lateinit var database: AppDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBaseBinding.inflate(layoutInflater, container, false)
        (activity as MainView?)?.showBottomBar()

        baseViewModel = ViewModelProvider(
            this@BaseFragment,
            ViewModelFactory(binding.root.context)
        )[BaseViewModel::class.java]
        setOnClickListeners()
        setUpPager()

        binding.recentRv.adapter = recentAdapter

        if (!baseViewModel.isObserved()) {
            observeViewModel()
        }

        loadRecent()

        return binding.root
    }

    private fun loadRecent() {
        database = AppDatabase.getInstance(binding.root.context)
        GlobalScope.launch(Dispatchers.Main) {
            val recentMovies = database.movieDao().getRecentMovies()
            val sortedList = recentMovies.sortedByDescending { it.index }
            if (sortedList.isNotEmpty()) {
                binding.recentTv.visibility = View.VISIBLE
                binding.recentRv.visibility = View.VISIBLE
                recentAdapter.submitList(sortedList)
            }
        }
    }


    private fun observeViewModel() {
        GlobalScope.launch(Dispatchers.Main) {
            baseViewModel.movieIntent.send(MovieIntent.FetchMovie)
            baseViewModel.state.collect {
                when (it) {
                    is MovieViewState.MovieIdle -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    is MovieViewState.MovieLoadingState -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is MovieViewState.MovieLoadedState -> {
                        binding.progressBar.visibility = View.GONE
                        pagerAdapter.submitList(it.list)
                    }
                    is MovieViewState.MovieNoItemState -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(binding.root.context, "No Item", Toast.LENGTH_SHORT).show()
                    }
                    is MovieViewState.MovieErrorState -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(binding.root.context, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.apply {
            searchIv.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SearchFragment()).addToBackStack("search")
                    .commit()
            }
        }
    }

    private fun setUpPager() {
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.setPageTransformer(CardTransformer(binding.root.context))

        val itemDecoration = HorizontalMarginItemDecoration(
            binding.root.context,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        binding.viewPager.addItemDecoration(itemDecoration)

    }
}