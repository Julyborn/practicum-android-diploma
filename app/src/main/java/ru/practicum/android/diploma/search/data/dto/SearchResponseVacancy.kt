package ru.practicum.android.diploma.search.data.dto

import ru.practicum.android.diploma.filter.data.dto.IndustryDto

data class SearchResponseVacancy(
    val id: String, // Идентификатор вакансии
    val name: String, // Название вакансии
    val area: AreaDto?, // Регион
    val salary: SalaryDto?, // Зарплата
    val description: String, // Описание вакансии
    val industryId: IndustryDto?,
    val selectedCountry: String,
    val selectedRegion: String?,
    val employer: EmployerDto? // Logo
)
