package com.udacity.asteroidradar.manager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.getPreviousDay
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException

class AsteroidWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "AsteroidWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database, Network.apiService)
        database.asteroidDao.clear(getPreviousDay())
        database.imageOfTheDayDao.clear()
        return try {
            repository.getNextSevenDataFromNetwork()
            repository.getImageFromNetwork()
            Result.success()
        } catch (exception: HttpException) {
            Result.retry()
        }
    }
}