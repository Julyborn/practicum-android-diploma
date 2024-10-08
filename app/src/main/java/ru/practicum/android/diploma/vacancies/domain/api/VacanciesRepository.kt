package ru.practicum.android.diploma.vacancies.domain.api

import ru.practicum.android.diploma.vacancies.domain.models.VacancyDetails

interface VacanciesRepository {
    suspend fun getVacancyDetails(vacancyId: String): VacancyDetails
}
