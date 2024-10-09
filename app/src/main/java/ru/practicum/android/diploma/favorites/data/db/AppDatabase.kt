package ru.practicum.android.diploma.favorites.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.practicum.android.diploma.favorites.data.db.dao.VacancyDetailsDao
import ru.practicum.android.diploma.favorites.data.db.entity.VacancyDetailsEntity

@Database(
    version = 1,
    entities = [
        VacancyDetailsEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getVacancyDetails(): VacancyDetailsDao
}
