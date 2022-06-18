package com.abdulaziz.movieplus.data.models.images

data class Images(
    val id: Int,
    val backdrops: List<Backdrop>,
    val logos: List<Logo>,
    val posters: List<Poster>
)