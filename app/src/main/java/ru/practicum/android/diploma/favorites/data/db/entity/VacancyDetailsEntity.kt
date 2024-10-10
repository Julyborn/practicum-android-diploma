package ru.practicum.android.diploma.favorites.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class VacancyDetailsEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val salaryJson: String?,
    val employerJson: String?,
    val address: String?,
    val areaName: String?,
    val description: String,
    val skills: String?,
    val experience: String?,
    val employment: String?,
)
