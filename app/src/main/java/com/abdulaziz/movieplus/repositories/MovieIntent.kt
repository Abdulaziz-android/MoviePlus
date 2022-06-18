package com.abdulaziz.movieplus.repositories

sealed class MovieIntent {
    object FetchMovie:MovieIntent()
    object FetchRecentMovies:MovieIntent()
    object FetchImages:MovieIntent()
}