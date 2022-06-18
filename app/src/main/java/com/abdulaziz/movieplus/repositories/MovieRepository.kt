package com.abdulaziz.movieplus.repositories

import com.abdulaziz.movieplus.data.local.AppDatabase
import com.abdulaziz.movieplus.data.models.movie.Movie
import com.abdulaziz.movieplus.data.models.trending.MovieSimple
import com.abdulaziz.movieplus.data.network.ApiService

class MovieRepository(
    private val apiService: ApiService,
    private val database: AppDatabase
) {
    // BaseFragment repositories
    suspend fun getRemoteMovies() = apiService.getPopularMovies()
    suspend fun saveMovies(list: List<MovieSimple>) = database.moviesDao().insert(list)
    suspend fun getLocalMovies() = database.moviesDao().getMovies()

    // ShowFragment repositories
    suspend fun getRemoteMovie(id:Int) = apiService.getMovieById(movie_id = id)
    suspend fun getLocalMovie(id:Int) = database.movieDao().getMovieById(id)
    suspend fun saveMovie(movie: Movie) = database.movieDao().insert(movie)
    suspend fun getMovieImages(id:Int) = apiService.getMovieImages(movie_id = id)
    suspend fun getLastIndex() = database.movieDao().getLastMovieIndex()

    // SearchFragment repositories
    suspend fun getSearchedMovie(query:String) = apiService.getSearchedMovie(query = query)

}