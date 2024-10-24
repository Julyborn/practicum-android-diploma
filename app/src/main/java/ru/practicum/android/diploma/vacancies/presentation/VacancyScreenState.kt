package ru.practicum.android.diploma.vacancies.presentation

import ru.practicum.android.diploma.vacancies.domain.models.VacancyDetails

sealed class VacancyScreenState {
    data object Loading : VacancyScreenState()
    data class Success(val vacancyDetails: VacancyDetails) : VacancyScreenState()
    data class Error(val errorType: ErrorType) : VacancyScreenState()
    enum class ErrorType {
        NOT_FOUND,
        SERVER_ERROR,
        NO_INTERNET
    }
}
