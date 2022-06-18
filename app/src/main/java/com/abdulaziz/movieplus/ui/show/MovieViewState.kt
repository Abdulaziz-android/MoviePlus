package com.abdulaziz.movieplus.ui.show

import com.abdulaziz.movieplus.data.models.images.Images
import com.abdulaziz.movieplus.data.models.movie.Movie

sealed class MovieDetailsViewState{
    object MovieIdle:MovieDetailsViewState()
    object MovieLoadingState:MovieDetailsViewState()
    object MovieNoItemState:MovieDetailsViewState()
    class MovieLoadedState(val movie:Movie):MovieDetailsViewState()
    class MovieImagesLoadedState(val images: Images):MovieDetailsViewState()
    class MovieErrorState(val message:String):MovieDetailsViewState()
}