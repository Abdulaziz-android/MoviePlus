package com.abdulaziz.movieplus.data.models.trending

data class Movies(
    val page: Int,
    val results: List<MovieSimple>,
    val total_pages: Int,
    val total_results: Int
)