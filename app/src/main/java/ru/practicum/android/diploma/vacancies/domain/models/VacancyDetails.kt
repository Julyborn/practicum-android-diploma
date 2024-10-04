package ru.practicum.android.diploma.vacancies.domain.models

import ru.practicum.android.diploma.search.domain.models.Employer
import ru.practicum.android.diploma.search.domain.models.Salary

data class VacancyDetails(
    val id: String,
    val name: String,
    val salary: Salary?,
    val employer: Employer?,
    val address: String?,
    val areaName: String?,
    val description: String,
    val skills: String?,
    val experience: String?,
    val employment: String?,
)
