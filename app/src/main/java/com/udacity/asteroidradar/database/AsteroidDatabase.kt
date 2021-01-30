package com.udacity.asteroidradar.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.Asteroid

@JsonClass(generateAdapter = true)
@Entity(tableName = "asteroid_table")
data class AsteroidDatabase constructor(
        @PrimaryKey(autoGenerate = true)
        val id: Long,

        @ColumnInfo(name = "code_name")
        val codename: String,

        @ColumnInfo(name = "close_approach_date")
        val closeApproachDate: String,

        @ColumnInfo(name = "absolute_magnitude")
        val absoluteMagnitude: Double,

        @ColumnInfo(name = "estimated_diameter")
        val estimatedDiameter: Double,

        @ColumnInfo(name = "relative_velocity")
        val relativeVelocity: Double,

        @ColumnInfo(name = "distance_from_earth")
        val distanceFromEarth: Double,

        @ColumnInfo(name = "is_potentially_hazardous")
        val isPotentiallyHazardous: Boolean
)

fun List<AsteroidDatabase>.asDomainModel(): List<Asteroid> {
    return map {
        Asteroid(
                id = it.id,
                codename = it.codename,
                closeApproachDate = it.closeApproachDate,
                absoluteMagnitude = it.absoluteMagnitude,
                estimatedDiameter = it.estimatedDiameter,
                relativeVelocity = it.relativeVelocity,
                distanceFromEarth = it.distanceFromEarth,
                isPotentiallyHazardous = it.isPotentiallyHazardous)
    }
}

fun List<Asteroid>.asDatabaseModel(): Array<AsteroidDatabase> {
    return map {
        AsteroidDatabase(
                id = it.id,
                codename = it.codename,
                closeApproachDate = it.closeApproachDate,
                absoluteMagnitude = it.absoluteMagnitude,
                estimatedDiameter = it.estimatedDiameter,
                relativeVelocity = it.relativeVelocity,
                distanceFromEarth = it.distanceFromEarth,
                isPotentiallyHazardous = it.isPotentiallyHazardous)
    }.toTypedArray()
}