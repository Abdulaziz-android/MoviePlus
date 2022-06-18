package com.abdulaziz.movieplus.ui.show

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdulaziz.movieplus.data.models.movie.Movie
import com.abdulaziz.movieplus.repositories.MovieIntent
import com.abdulaziz.movieplus.repositories.MovieRepository
import com.abdulaziz.movieplus.utils.NetworkHelper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class ShowViewModel(
    private val repository: MovieRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    val movieIntent = Channel<MovieIntent>(Channel.UNLIMITED)
    val stateData = MutableStateFlow<MovieDetailsViewState>(MovieDetailsViewState.MovieIdle)
    val stateImages = MutableStateFlow<MovieDetailsViewState>(MovieDetailsViewState.MovieIdle)
    var movieId = MutableLiveData<Int>()
    private var movieLiveData = MutableLiveData<Movie>()

    init {
        handleIntent()
    }

    fun setMovieId(id: Int) {
        movieId.value = id
    }

    private fun handleIntent() {
        viewModelScope.launch {
            movieIntent.consumeAsFlow().collect {
                when (it) {
                    is MovieIntent.FetchMovie -> fetchMovieData()
                    is MovieIntent.FetchImages -> fetchMovieImages()
                }
            }
        }
    }

    fun saveMovie(isSave: Boolean) {
        viewModelScope.launch {
            movieLiveData.value?.isSaved = isSave
            movieLiveData.value?.let { repository.saveMovie(movie = it) }
        }
    }

    private fun fetchMovieImages() {
        viewModelScope.launch {
            stateImages.value = MovieDetailsViewState.MovieLoadingState
            stateImages.value = try {
                if (networkHelper.isNetworkConnected()) {
                    val remoteImages = repository.getMovieImages(movieId.value!!)
                    if (remoteImages.backdrops.isNotEmpty())
                        MovieDetailsViewState.MovieImagesLoadedState(remoteImages)
                    else MovieDetailsViewState.MovieNoItemState
                } else {
                    MovieDetailsViewState.MovieErrorState("No Internet Connection!")
                }
            } catch (e: Exception) {
                MovieDetailsViewState.MovieErrorState(e.localizedMessage)
            }
        }
    }

    private fun fetchMovieData() {
        viewModelScope.launch {
            stateData.value = MovieDetailsViewState.MovieLoadingState
            val local = repository.getLocalMovie(movieId.value!!)
            val index = if (repository.getLastIndex()!=null) repository.getLastIndex() else -1
            var lastIndex = 0
            if (local!=null) {
                lastIndex =
                    if (index == -1) 0 else if (index == local.index) index else index!! + 1
            }else {
                lastIndex = if (index == -1) 0 else index!! + 1
            }
            stateData.value = try {
                if (networkHelper.isNetworkConnected()) {
                    val remoteMovie = repository.getRemoteMovie(movieId.value!!)
                    if (local == null) {
                        remoteMovie.index = lastIndex
                    } else {
                        remoteMovie.index = lastIndex
                        remoteMovie.isSaved = local.isSaved
                    }
                    repository.saveMovie(remoteMovie)
                    movieLiveData.postValue(remoteMovie)
                    MovieDetailsViewState.MovieLoadedState(remoteMovie)
                } else {
                    if (local != null) {
                        local.index = lastIndex
                        movieLiveData.postValue(local)
                        repository.saveMovie(local)
                        MovieDetailsViewState.MovieLoadedState(local)
                    } else {
                        MovieDetailsViewState.MovieErrorState("No Internet Connection!")
                    }
                }
            } catch (e: Exception) {
                MovieDetailsViewState.MovieErrorState(e.localizedMessage)
            }
        }
    }


}