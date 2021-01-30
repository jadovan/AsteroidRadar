package com.udacity.asteroidradar.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.util.Constants.API_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AsteroidRepository(private val database: AsteroidsDatabase) {

    @RequiresApi(Build.VERSION_CODES.O)
    private val beginDate = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    private val endDate = LocalDateTime.now().plusDays(7)

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroids()) {
        it.asDomainModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val todaysAsteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao
            .getTodaysAsteroids(beginDate.format(DateTimeFormatter.ISO_DATE))) {
        it.asDomainModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val asteroidsByDate: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao
            .getAsteroidsByDate(beginDate.format(DateTimeFormatter.ISO_DATE),
                    endDate.format(DateTimeFormatter.ISO_DATE))) {
        it.asDomainModel()
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroid = AsteroidApi.retrofitService.getAsteroids(API_KEY)
                val parseAsteroids = parseAsteroidsJsonResult(JSONObject(asteroid))
                database.asteroidDao.insertAll(*parseAsteroids.asDatabaseModel())
            } catch (error: Exception) {
                Log.e("refreshAsteroids error", error.message.toString())
            }
        }
    }

}