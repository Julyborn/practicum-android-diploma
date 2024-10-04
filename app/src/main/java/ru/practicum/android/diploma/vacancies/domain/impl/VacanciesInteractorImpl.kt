package ru.practicum.android.diploma.vacancies.domain.impl

import ru.practicum.android.diploma.vacancies.domain.api.VacanciesInteractor
import ru.practicum.android.diploma.vacancies.domain.api.VacanciesRepository
import ru.practicum.android.diploma.vacancies.domain.models.VacancyDetails

class VacanciesInteractorImpl(private val vacanciesRepository: VacanciesRepository) : VacanciesInteractor {
    override suspend fun getVacancyDetails(vacancyId: String): VacancyDetails {
        return vacanciesRepository.getVacancyDetails(vacancyId)
    }
}
