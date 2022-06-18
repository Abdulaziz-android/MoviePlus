package com.abdulaziz.movieplus.data.models

import com.abdulaziz.movieplus.data.models.trending.MovieSimple

data class SearchedMovies(
    val page:Int,
    val results:List<MovieSimple>,
    val total_pages:Int,
    val total_results:Int,
)
