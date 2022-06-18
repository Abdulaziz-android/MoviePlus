package com.abdulaziz.movieplus.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abdulaziz.movieplus.data.models.movie.Movie

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: Movie)

    @Query("select * from movie where id = :id")
    suspend fun getMovieById(id:Int):Movie?

    @Query("select * from movie where isSaved = 1")
    suspend fun getSavedMovies():List<Movie>

    @Query("SELECT MAX(`index`) FROM movie")
    suspend fun getLastMovieIndex(): Int?

    @Query("select * from movie")
    suspend fun getRecentMovies(): List<Movie>

}