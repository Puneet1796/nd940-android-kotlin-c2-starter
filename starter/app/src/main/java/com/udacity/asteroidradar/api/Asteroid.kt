package com.udacity.asteroidradar.api

import android.os.Parcelable
import com.udacity.asteroidradar.database.AsteroidEntity
import com.udacity.asteroidradar.database.ImageOfTheDayEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class Asteroid(
    val id: Long,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
) : Parcelable

@Parcelize
data class ImageOfTheDay(
    val url: String,
    val title: String,
    val mediaType: String
) : Parcelable {
    val isImage: Boolean
        get() = mediaType.equals("image", ignoreCase = true)
}

fun ImageOfTheDay.asDatabaseDomain(): ImageOfTheDayEntity {
    return ImageOfTheDayEntity(
        title = title,
        imageUrl = url,
        isImage = isImage,
        mediaType = mediaType
    )
}

fun Asteroid.asDatabaseDomain(): AsteroidEntity {
    return AsteroidEntity(
        id,
        codename,
        closeApproachDate,
        absoluteMagnitude,
        estimatedDiameter,
        isPotentiallyHazardous,
        relativeVelocity,
        distanceFromEarth
    )
}