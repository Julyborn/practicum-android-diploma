package ru.practicum.android.diploma.favorites.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.vacancies.domain.models.VacancyDetails

interface FavoritesInteractor {
    fun getAll(): Flow<List<VacancyDetails>>
    suspend fun addVacancy(vacancyDetails: VacancyDetails)
    suspend fun removeVacancy(id: String)
    suspend fun getVacancyById(id: String): VacancyDetails?
}
