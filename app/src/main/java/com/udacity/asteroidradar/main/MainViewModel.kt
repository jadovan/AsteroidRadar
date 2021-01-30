package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import com.udacity.asteroidradar.util.Constants.API_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)
    private val asteroidList = asteroidRepository.asteroids

    private val _asteroids = MutableLiveData<Asteroid>()
    private val _asteroidPictureOfDay = MutableLiveData<PictureOfDay>()
    private val _asteroidFilter = MutableLiveData<AsteroidFilter>(AsteroidFilter.SAVED)

    val asteroids: LiveData<Asteroid>
        get() = _asteroids

    val asteroidPictureOfDay: LiveData<PictureOfDay>
        get() = _asteroidPictureOfDay

    val asteroidFilter = Transformations.switchMap(_asteroidFilter) {
        when (it!!) {
            AsteroidFilter.NEXTWEEK -> asteroidRepository.asteroidsByDate
            AsteroidFilter.TODAYS -> asteroidRepository.todaysAsteroids
            else -> asteroidList
        }
    }

    init {
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
            showPictureOfDay()
        }
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _asteroids.value = asteroid
    }

    fun onAsteroidNavigated() {
        _asteroids.value = null
    }

    fun onAsteroidFiltered(asteroidFilter: AsteroidFilter) {
        _asteroidFilter.postValue(asteroidFilter)
    }

    private suspend fun showPictureOfDay() {
        withContext(Dispatchers.IO) {
            _asteroidPictureOfDay.postValue(AsteroidApi.retrofitService.getAsteroidPictureOfDay(API_KEY))
        }
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

    enum class AsteroidFilter {
        TODAYS, NEXTWEEK, SAVED
    }
}