package com.abdulaziz.movieplus.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdulaziz.movieplus.repositories.MovieRepository
import com.abdulaziz.movieplus.ui.view_states.MovieViewState
import com.abdulaziz.movieplus.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: MovieRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    //val movieIntent = Channel<MovieIntent>(Channel.UNLIMITED)
    val state = MutableStateFlow<MovieViewState>(MovieViewState.MovieIdle)

    fun fetchMovies(query: String) {
        viewModelScope.launch {
            state.value = MovieViewState.MovieLoadingState
            state.value = try {
                if (networkHelper.isNetworkConnected()) {
                    val remoteMovies = repository.getSearchedMovie(query = query)
                    if (remoteMovies.results.isNotEmpty())
                        MovieViewState.MovieLoadedState(remoteMovies.results)
                    else MovieViewState.MovieNoItemState
                } else {
                    MovieViewState.MovieErrorState("No Internet Connection!")
                }
            } catch (e: Exception) {
                MovieViewState.MovieErrorState(e.localizedMessage?:e.message?:"Error")
            }
        }
    }

    fun isObserved(): Boolean {
        return state.value != MovieViewState.MovieIdle
    }


}