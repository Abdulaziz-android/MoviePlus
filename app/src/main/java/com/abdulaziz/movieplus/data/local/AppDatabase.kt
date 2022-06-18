package com.abdulaziz.movieplus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.abdulaziz.movieplus.data.models.movie.CompanyConverter
import com.abdulaziz.movieplus.data.models.movie.GenreConverter
import com.abdulaziz.movieplus.data.models.movie.Movie
import com.abdulaziz.movieplus.data.models.trending.MovieSimple

@Database(entities = [MovieSimple::class, Movie::class], version = 1)
@TypeConverters(GenreConverter::class, CompanyConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun moviesDao(): MoviesDao
    abstract fun movieDao(): MovieDao

    companion object {
        fun getInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "database")
                .allowMainThreadQueries()
                .build()
        }
    }

}