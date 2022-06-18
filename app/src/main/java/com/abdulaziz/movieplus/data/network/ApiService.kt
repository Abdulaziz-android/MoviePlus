package com.abdulaziz.movieplus.data.network

import com.abdulaziz.movieplus.data.models.SearchedMovies
import com.abdulaziz.movieplus.data.models.images.Images
import com.abdulaziz.movieplus.data.models.movie.Movie
import com.abdulaziz.movieplus.data.models.trending.Movies
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("media_type") media_type: String = "movie",
        @Query("api_key") api_key: String = "59a02a732ba859a065a5860b822ffbe4",
    ): Movies

    @GET("movie/{movie_id}")
    suspend fun getMovieById(
        @Path(value = "movie_id", encoded = false) movie_id: Int,
        @Query("api_key") api_key: String = "59a02a732ba859a065a5860b822ffbe4",
    ): Movie

    @GET("movie/{movie_id}/images")
    suspend fun getMovieImages(
        @Path(value = "movie_id", encoded = false) movie_id: Int,
        @Query("api_key") api_key: String = "59a02a732ba859a065a5860b822ffbe4",
    ): Images

    @GET("search/movie")
    suspend fun getSearchedMovie(
        @Query("query") query: String,
        @Query("api_key") api_key: String = "59a02a732ba859a065a5860b822ffbe4",
    ): SearchedMovies

}