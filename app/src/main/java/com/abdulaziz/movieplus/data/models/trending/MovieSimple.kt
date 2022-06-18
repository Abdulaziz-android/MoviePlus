package com.abdulaziz.movieplus.data.models.trending

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class MovieSimple(
    @PrimaryKey
    val id: Int,
    val adult: Boolean,
    val backdrop_path: String,/*
    @TypeConverters(GenreConverter::class)
    val genre_ids: List<Int>?=null,*/
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String,
    val release_date: String,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int
):Serializable