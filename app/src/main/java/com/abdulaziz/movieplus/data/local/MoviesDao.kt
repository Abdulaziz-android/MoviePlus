package com.abdulaziz.movieplus.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abdulaziz.movieplus.data.models.trending.MovieSimple

@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<MovieSimple>)

    @Query("select * from moviesimple")
    suspend fun getMovies():List<MovieSimple>

}