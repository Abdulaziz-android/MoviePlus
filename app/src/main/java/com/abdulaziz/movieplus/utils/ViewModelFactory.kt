package com.abdulaziz.movieplus.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abdulaziz.movieplus.data.local.AppDatabase
import com.abdulaziz.movieplus.data.network.ApiClient
import com.abdulaziz.movieplus.repositories.MovieRepository
import com.abdulaziz.movieplus.ui.base.BaseViewModel
import com.abdulaziz.movieplus.ui.search.SearchViewModel
import com.abdulaziz.movieplus.ui.show.ShowViewModel


class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val apiService = ApiClient.apiService
        val database = AppDatabase.getInstance(context)
        val networkHelper = NetworkHelper(context)
        if (modelClass.isAssignableFrom(BaseViewModel::class.java)) {
            return BaseViewModel(MovieRepository(apiService, database), networkHelper) as T
        }
        else if (modelClass.isAssignableFrom(ShowViewModel::class.java)) {
                return ShowViewModel(MovieRepository(apiService, database), networkHelper) as T
        }
        else if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                return SearchViewModel(MovieRepository(apiService, database), networkHelper) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}