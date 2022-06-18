package com.abdulaziz.movieplus.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdulaziz.movieplus.repositories.MovieIntent
import com.abdulaziz.movieplus.repositories.MovieRepository
import com.abdulaziz.movieplus.ui.view_states.MovieViewState
import com.abdulaziz.movieplus.utils.NetworkHelper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class BaseViewModel(
    private val repository: MovieRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    val movieIntent = Channel<MovieIntent>(Channel.UNLIMITED)
    val state = MutableStateFlow<MovieViewState>(MovieViewState.MovieIdle)

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            movieIntent.consumeAsFlow().collect {
                when (it) {
                    is MovieIntent.FetchMovie -> fetchMovies()
                    else -> {}
                }
            }
        }
    }

    private fun fetchMovies() {
        viewModelScope.launch {
            state.value = MovieViewState.MovieLoadingState
            state.value = try {
                if (networkHelper.isNetworkConnected()) {
                    val remoteMovies = repository.getRemoteMovies()
                    repository.saveMovies(remoteMovies.results)
                    MovieViewState.MovieLoadedState(remoteMovies.results)
                } else {
                    val localMovies = repository.getLocalMovies()
                    if (localMovies.isNotEmpty()) {
                        MovieViewState.MovieLoadedState(localMovies)
                    } else {
                        MovieViewState.MovieErrorState("No Internet Connection!")
                    }
                }
            } catch (e: Exception) {
                MovieViewState.MovieErrorState(e.localizedMessage)
            }
        }
    }

    fun isObserved():Boolean{
        return state.value != MovieViewState.MovieIdle
    }

}