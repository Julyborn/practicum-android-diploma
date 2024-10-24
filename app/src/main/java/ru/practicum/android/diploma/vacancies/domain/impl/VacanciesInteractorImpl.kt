package ru.practicum.android.diploma.vacancies.domain.impl

import ru.practicum.android.diploma.favorites.domain.api.FavoritesRepository
import ru.practicum.android.diploma.search.data.network.NetworkUtils
import ru.practicum.android.diploma.vacancies.domain.api.VacanciesInteractor
import ru.practicum.android.diploma.vacancies.domain.api.VacanciesRepository
import ru.practicum.android.diploma.vacancies.domain.models.VacancyDetails
import java.io.IOException

class VacanciesInteractorImpl(
    private val vacanciesRepository: VacanciesRepository,
    private val favoritesRepository: FavoritesRepository
) : VacanciesInteractor {
    override suspend fun getVacancyDetails(vacancyId: String): VacancyDetails {
        return try {
            vacanciesRepository.getVacancyDetails(vacancyId)
        } catch (e: NetworkUtils.NoInternetException) {
            val vacancyDetails = favoritesRepository.getVacancyById(vacancyId)
            vacancyDetails ?: throw e
        } catch (e: IOException) {
            val vacancyDetails = favoritesRepository.getVacancyById(vacancyId)
            vacancyDetails ?: throw e
        }
    }
}
