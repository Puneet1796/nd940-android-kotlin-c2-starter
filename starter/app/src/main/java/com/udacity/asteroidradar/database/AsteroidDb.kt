package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.api.Asteroid
import com.udacity.asteroidradar.api.ImageOfTheDay

@Entity(tableName = "asteroids_table")
data class AsteroidEntity(
    @PrimaryKey val id: Long = 0L,
    @ColumnInfo val codename: String,
    @ColumnInfo(name = "close_approach_date") val closeApproachDate: String,
    @ColumnInfo(name = "absolute_magnitude") val absoluteMagnitude: Double,
    @ColumnInfo(name = "estimated_diameter_max_in_km") val estimatedDiameter: Double,
    @ColumnInfo(name = "is_potentially_hazardous_asteroid") val isPotentiallyHazardous: Boolean,
    @ColumnInfo(name = "relative_velocity_kms") val relativeVelocity: Double,
    @ColumnInfo(name = "distance_from_earth_astronomical") val distanceFromEarth: Double
)

@Entity(tableName = "image_of_the_table")
data class ImageOfTheDayEntity(
    @PrimaryKey val id: Long = 0L,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "url") val imageUrl: String,
    @ColumnInfo(name = "media_type") val mediaType: String,
    @ColumnInfo(name = "is_image") val isImage: Boolean
)

fun List<AsteroidEntity>.asDomainModel(): List<Asteroid> {
    return map {
        Asteroid(
            it.id,
            it.codename,
            it.closeApproachDate,
            it.absoluteMagnitude,
            it.estimatedDiameter,
            it.relativeVelocity,
            it.distanceFromEarth,
            it.isPotentiallyHazardous
        )
    }
}

fun ImageOfTheDayEntity.asDomainModel(): ImageOfTheDay {
    return ImageOfTheDay(
        imageUrl,
        title,
        mediaType
    )
}

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM asteroids_table")
    fun get(): List<AsteroidEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg asteroid: AsteroidEntity)

    @Query("DELETE FROM asteroids_table WHERE close_approach_date = :date")
    suspend fun clear(date: String)
}

@Dao
interface ImageOfTheDayDao {
    @Query("SELECT * FROM image_of_the_table")
    fun get(): LiveData<ImageOfTheDayEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(imageOfTheDayEntity: ImageOfTheDayEntity)

    @Query("DELETE FROM image_of_the_table")
    suspend fun clear()
}

@Database(entities = [AsteroidEntity::class, ImageOfTheDayEntity::class], version = 1)
abstract class AsteroidDb : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
    abstract val imageOfTheDayDao: ImageOfTheDayDao
}

private lateinit var INSTANCE: AsteroidDb

fun getDatabase(context: Context): AsteroidDb {
    synchronized(AsteroidDb::class) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidDb::class.java,
                "asteroids_db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
        return INSTANCE
    }
}