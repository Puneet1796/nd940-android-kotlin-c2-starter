package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

enum class AsteroidApiFilter { SHOW_WEEK, SHOW_TODAY, SHOW_SAVED }

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val repository = AsteroidRepository(database, Network.apiService)

    private val _progressBar = MutableLiveData<Boolean>()
    val progressBar: LiveData<Boolean>
        get() = _progressBar

    val errorContainer = repository.errorContainer

    init {
        viewModelScope.launch {
            _progressBar.postValue(true)
            repository.getNextSevenDataFromNetwork()
            _progressBar.postValue(false)
        }
        getImageData()
    }

    val asteroids = repository.asteroids

    val imageOfTheDay = Transformations.map(repository.imageOfTheDay) {
        it?.asDomainModel()
    }

    fun updateFilter(filter: AsteroidApiFilter) {
        _progressBar.value = true
        viewModelScope.launch {
            when (filter) {
                AsteroidApiFilter.SHOW_TODAY -> {
                    repository.getTodayDataFromNetwork()
                }
                AsteroidApiFilter.SHOW_WEEK -> {
                    repository.getNextSevenDataFromNetwork()
                }
                else -> repository.getDataFromDatabase()
            }
            _progressBar.postValue(false)
        }
    }

    private fun getImageData() {
        viewModelScope.launch {
            repository.getImageFromNetwork()
        }
    }
}