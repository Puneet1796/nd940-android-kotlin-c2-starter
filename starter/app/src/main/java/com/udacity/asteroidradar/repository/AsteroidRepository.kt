package com.udacity.asteroidradar.repository

import com.udacity.asteroidradar.api.Asteroid
import com.udacity.asteroidradar.api.ImageOfTheDay
import com.udacity.asteroidradar.api.NetworkService
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.api.parseImageOfTheDayJsonResult
import com.udacity.asteroidradar.api.asDatabaseDomain
import com.udacity.asteroidradar.database.AsteroidDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(
    private val database: AsteroidDb,
    private val apiService: NetworkService
) {
    val asteroids = database.asteroidDao.get()
    val imageOfTheDay = database.imageOfTheDayDao.get()

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

    suspend fun getDataFromNetwork() {
        withContext(Dispatchers.IO) {
            val response = apiService.getFeed("", "")
            insert(parseAsteroidsJsonResult(JSONObject(response)))
        }
    }

    suspend fun getImageFromNetwork() {
        withContext(Dispatchers.IO) {
            val response = apiService.getImageOfTheDay()
            insert(parseImageOfTheDayJsonResult(JSONObject(response)))
        }
    }
}