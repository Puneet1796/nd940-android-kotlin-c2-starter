package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AsteroidDb
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.getToday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.net.UnknownHostException

class AsteroidRepository(
    private val database: AsteroidDb,
    private val apiService: NetworkService
) {
    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids
    val imageOfTheDay = database.imageOfTheDayDao.get()

    private val _errorContainer = MutableLiveData<Boolean>()
    val errorContainer: LiveData<Boolean>
        get() = _errorContainer

    private suspend fun insert(listOfAsteroid: List<Asteroid>) {
        withContext(Dispatchers.IO) {
            database.asteroidDao.insertAll(
                *listOfAsteroid.map { it.asDatabaseDomain() }
                    .toTypedArray())
        }
    }

    private suspend fun insert(imageOfTheDay: ImageOfTheDay) {
        withContext(Dispatchers.IO) {
            database.imageOfTheDayDao.clear()
            database.imageOfTheDayDao.insert(imageOfTheDay.asDatabaseDomain())
        }
    }

    suspend fun getDataFromDatabase() {
        withContext(Dispatchers.IO) {
            val list = database.asteroidDao.get().asDomainModel()
            if (list.isEmpty()) {
                _errorContainer.postValue(true)
            } else {
                _errorContainer.postValue(false)
            }
            _asteroids.postValue(list)
        }
    }

    suspend fun getTodayDataFromNetwork() {
        withContext(Dispatchers.IO) {
            _errorContainer.postValue(false)
            try {
                val today = getToday()
                val startDate = today.first
                val endData = today.second
                val response = apiService.getFeed(startDate, endData)
                val listOfAsteroid = parseAsteroidsJsonResult(JSONObject(response)).toList()
                _asteroids.postValue(listOfAsteroid)
                insert(parseAsteroidsJsonResult(JSONObject(response)))
            } catch (e: UnknownHostException) {
                _errorContainer.postValue(true)
            }
        }
    }

    suspend fun getNextSevenDataFromNetwork() {
        withContext(Dispatchers.IO) {
            _errorContainer.postValue(false)
            try {
                val startDate = getNextSevenDaysFormattedDates().first()
                val endDate = getNextSevenDaysFormattedDates().last()
                val response = apiService.getFeed(startDate, endDate)
                val listOfAsteroid = parseAsteroidsJsonResult(JSONObject(response)).toList()
                _asteroids.postValue(listOfAsteroid)
                insert(parseAsteroidsJsonResult(JSONObject(response)))
            } catch (e: UnknownHostException) {
                _errorContainer.postValue(true)
            }
        }
    }

    suspend fun getImageFromNetwork() {
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getImageOfTheDay()
                insert(parseImageOfTheDayJsonResult(JSONObject(response)))
            } catch (e: UnknownHostException) {
                Timber.e(e)
            }
        }
    }
}