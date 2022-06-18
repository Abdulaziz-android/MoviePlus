package com.abdulaziz.movieplus.ui.show

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.abdulaziz.movieplus.databinding.FragmentShowBinding
import com.abdulaziz.movieplus.ui.main.MainView
import com.squareup.picasso.Picasso
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.abdulaziz.movieplus.R
import com.abdulaziz.movieplus.adapters.ImageAdapter
import com.abdulaziz.movieplus.data.models.movie.Movie
import com.abdulaziz.movieplus.repositories.MovieIntent
import com.abdulaziz.movieplus.ui.main.MainActivity
import com.abdulaziz.movieplus.utils.ViewModelFactory
import com.google.android.material.appbar.AppBarLayout
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.pow


private const val ARG_PARAM1 = "movie_id"
private const val ARG_PARAM2 = "param2"


class ShowFragment : Fragment(), AppBarLayout.OnOffsetChangedListener {

    private var movie_id: Int? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            movie_id = it.getInt(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        (activity as MainView?)?.hideBottomBar()
    }

    private var state: State? = null
    private var _binding: FragmentShowBinding? = null
    private val binding get() = _binding!!
    private lateinit var showViewModel: ShowViewModel
    private var _movie:Movie?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowBinding.inflate(layoutInflater, container, false)
        prepareAll()
        return binding.root
    }

    private fun prepareAll() {
        showViewModel = ViewModelProvider(
            this@ShowFragment,
            ViewModelFactory(binding.root.context)
        )[ShowViewModel::class.java]
        observeViewModel()
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.mytoolbar)
        setOnClickListeners()
        binding.appbar.addOnOffsetChangedListener(this)
        getBlur()
    }

    private fun setOnClickListeners() {
        binding.apply {
            saveIv.setOnClickListener {
                if (_movie != null) {
                    _movie?.isSaved = !_movie?.isSaved!!
                    _movie?.isSaved?.let { it1 ->
                        showViewModel.saveMovie(it1)
                        setUpSaveView(it1)
                    }
                }
            }

            backIv.setOnClickListener {
                (activity as MainView?)?.backPressed()
            }
        }
    }

    private fun observeViewModel() {
        GlobalScope.launch(Dispatchers.Main) {
            showViewModel.setMovieId(movie_id!!)
            showViewModel.movieIntent.send(MovieIntent.FetchMovie)
            showViewModel.stateData.collect {
                when(it){
                    is MovieDetailsViewState.MovieIdle -> {
                    }
                    is MovieDetailsViewState.MovieLoadingState -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is MovieDetailsViewState.MovieLoadedState -> {
                        binding.progressBar.visibility = View.GONE
                        loadMovie(it.movie)
                        loadImages()
                    }
                    is MovieDetailsViewState.MovieNoItemState -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "No Item", Toast.LENGTH_SHORT).show()
                    }
                    is MovieDetailsViewState.MovieErrorState -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(binding.root.context, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    suspend fun loadImages() {
        showViewModel.movieIntent.send(MovieIntent.FetchImages)
        showViewModel.stateImages.collect {
            when(it){
                is MovieDetailsViewState.MovieIdle -> {
                    binding.imagesRv.visibility = View.GONE
                }
                is MovieDetailsViewState.MovieLoadingState -> {
                    binding.imagesRv.visibility = View.GONE
                }
                is MovieDetailsViewState.MovieImagesLoadedState -> {
                    binding.imagesRv.visibility = View.VISIBLE
                    val adapter = ImageAdapter(it.images.backdrops)
                    binding.imagesRv.adapter = adapter
                }
                is MovieDetailsViewState.MovieNoItemState -> {
                    binding.imagesRv.visibility = View.GONE
                }
                is MovieDetailsViewState.MovieErrorState -> {
                    Toast.makeText(binding.root.context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadMovie(movie: Movie) {
        _movie = movie
        binding.apply {
            setUpSaveView(movie.isSaved)
            Picasso.get().load("https://image.tmdb.org/t/p/w500/${movie.poster_path}").into(imageView)
            toolbarTitleTv.text = movie.original_title
            toolbarTitleTv.isSelected = true
            durationTv.text = "${movie.runtime} minutes"


            titleTv.text = movie.original_title

            val genre = StringBuilder()
            movie.genres.forEachIndexed { index, g ->
                genre.append("${g.name}" + if (index!=movie.genres.size-1) ", " else "")
            }
            genreTv.text = genre.toString()

            runtimeTv.text = "Runtime: ${movie.runtime/60} h ${movie.runtime%60} min"

            releasedDataTv.text = "Released: ${dataFormatter(movie.release_date)}"

            movie.imdb_id?.let { imdbTv.text = "IMDb: "+it.subSequence(2, movie.imdb_id.length) }

            if (movie.budget!=0) { budgetTv.text = "Budget: $${numberFormatter(movie.budget.toLong())}"
            }else budgetTv.visibility = View.GONE

            val companies = StringBuilder()
            movie.production_companies.forEachIndexed { index, prCompany ->
                companies.append("\n${prCompany.name}" + if (index!=movie.production_companies.size-1) ", " else "")
            }
            companiesTv.text = if (companies.isNotEmpty()) "Production companies: $companies" else ""

            descriptionTv.text = movie.overview


            ratingProgressBar.progress = (movie.vote_average*10).toInt()
            progressTitleTv.text = movie.vote_average.toString()
        }
    }

    private fun setUpSaveView(saved: Boolean) {
        if (saved) {
            binding.saveIv.setImageResource(R.drawable.ic_baseline_bookmark_24)
        }else{
            binding.saveIv.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
        }
    }

    private fun dataFormatter(data:String): String {
        val incomingDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val showedDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
        val date = incomingDateFormat.parse(data)
        return showedDateFormat.format(date)
    }


    private fun numberFormatter(count: Long): String {
        if (count < 1000) return "" + count
        val exp = (ln(count.toDouble()) / ln(1000.0)).toInt()
        return String.format(
            "%.1f %c",
            count / 1000.0.pow(exp.toDouble()),
            "kMGTPE"[exp - 1]
        )
    }


    private fun getBlur() {
        val radius = 1f
        val decorView = (activity as MainActivity).window.decorView
        val rootView = decorView.findViewById<View>(android.R.id.content) as ViewGroup
        val windowBackground = decorView.background
        binding.blurview.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(binding.root.context))
            .setBlurRadius(radius)
            .setBlurAutoUpdate(false)
            .setHasFixedTransformationMatrix(true)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        if (verticalOffset == 0) {
            if (state != State.EXPANDED) {
                setVisibilityLayoutViews(View.VISIBLE)
                setVisibilityToolbarViews(View.GONE)
            }
            state = State.EXPANDED
        } else if (abs(verticalOffset) >= appBarLayout?.totalScrollRange!!) {
            if (state != State.COLLAPSED) {
                setVisibilityLayoutViews(View.GONE)
                setVisibilityToolbarViews(View.VISIBLE)
            }
            state = State.COLLAPSED
        } else {
            if (state != State.IDLE) {
            }
            state = State.IDLE
        }
    }

    private fun setVisibilityLayoutViews(visibility:Int){
        binding.titleTv.visibility = visibility
        binding.genreTv.visibility = visibility
        binding.runtimeTv.visibility = visibility
    }

    private fun setVisibilityToolbarViews(visibility:Int){
        binding.toolbarTitleTv.visibility = visibility
        binding.durationTv.visibility = visibility
        binding.playIv.visibility = visibility
    }
}

private enum class State {
    EXPANDED, COLLAPSED, IDLE
}
