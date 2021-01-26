package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val repository = AsteroidRepository(database, Network.apiService)

    init {
        getData()
        getImageData()
    }

    val asteroids = Transformations.map(repository.asteroids) {
        it.asDomainModel()
    }

    val imageOfTheDay = Transformations.map(repository.imageOfTheDay) {
        it.asDomainModel()
    }

    private fun getData() {
        viewModelScope.launch {
            repository.getDataFromNetwork()
        }
    }

    private fun getImageData() {
        viewModelScope.launch {
            repository.getImageFromNetwork()
        }
    }
}