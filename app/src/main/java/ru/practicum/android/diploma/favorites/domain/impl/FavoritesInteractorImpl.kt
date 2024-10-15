package ru.practicum.android.diploma.favorites.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.favorites.domain.api.FavoritesInteractor
import ru.practicum.android.diploma.favorites.domain.api.FavoritesRepository
import ru.practicum.android.diploma.vacancies.domain.models.VacancyDetails

class FavoritesInteractorImpl(private val favoritesRepository: FavoritesRepository) : FavoritesInteractor {
    override fun getAll(): Flow<List<VacancyDetails>> {
        return favoritesRepository.getAll()
    }

    override suspend fun addVacancy(vacancyDetails: VacancyDetails) {
        favoritesRepository.addVacancy(vacancyDetails)
    }

    override suspend fun removeVacancy(id: String) {
        favoritesRepository.removeVacancy(id)
    }

    override suspend fun getVacancyById(id: String): VacancyDetails? {
        return favoritesRepository.getVacancyById(id)
    }

}
