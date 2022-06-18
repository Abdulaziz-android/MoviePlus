package com.abdulaziz.movieplus.ui.view_states

import com.abdulaziz.movieplus.data.models.trending.MovieSimple

sealed class MovieViewState{
    object MovieIdle:MovieViewState()
    object MovieLoadingState:MovieViewState()
    object MovieNoItemState:MovieViewState()
    class MovieLoadedState(val list:List<MovieSimple>):MovieViewState()
    class MovieErrorState(val message:String):MovieViewState()
}
