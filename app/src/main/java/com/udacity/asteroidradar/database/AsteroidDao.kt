package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM asteroid_table ORDER BY close_approach_date DESC")
    fun getAsteroids(): LiveData<List<AsteroidDatabase>>

    @Query("SELECT * FROM asteroid_table WHERE close_approach_date = :beginDate ORDER BY close_approach_date DESC")
    fun getTodaysAsteroids(beginDate: String): LiveData<List<AsteroidDatabase>>

    @Query("SELECT * FROM asteroid_table WHERE close_approach_date BETWEEN :beginDate AND :endDate ORDER BY close_approach_date DESC")
    fun getAsteroidsByDate(beginDate: String, endDate: String): LiveData<List<AsteroidDatabase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroid: AsteroidDatabase)
}

@Database(entities = [AsteroidDatabase::class], version = 1, exportSchema = false)
abstract class AsteroidsDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                    AsteroidsDatabase::class.java,
                    "asteroid_table").build()
        }
    }
    return INSTANCE
}