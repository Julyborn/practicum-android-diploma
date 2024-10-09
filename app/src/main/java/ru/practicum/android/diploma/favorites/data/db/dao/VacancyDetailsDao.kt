package ru.practicum.android.diploma.favorites.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.favorites.data.db.entity.VacancyDetailsEntity

@Dao
interface VacancyDetailsDao {
    @Query("SELECT * FROM favorites")
    fun getAll(): Flow<List<VacancyDetailsEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addVacancy(vacancy: VacancyDetailsEntity)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteVacancyById(id: String)

    @Query("SELECT * FROM favorites WHERE id = :id LIMIT 1")
    suspend fun getVacancyById(id: String): VacancyDetailsEntity?
}
