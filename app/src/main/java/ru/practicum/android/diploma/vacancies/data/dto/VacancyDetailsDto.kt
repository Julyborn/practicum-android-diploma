package ru.practicum.android.diploma.vacancies.data.dto

import com.google.gson.annotations.SerializedName
import ru.practicum.android.diploma.search.data.dto.AreaDto
import ru.practicum.android.diploma.search.data.dto.EmployerDto
import ru.practicum.android.diploma.search.data.dto.SalaryDto

data class VacancyDetailsDto(
    val id: String,
    val name: String,
    val salary: SalaryDto?,
    val employer: EmployerDto?,
    val address: AddressDto?,
    val area: AreaDto?,
    val description: String,
    @SerializedName("key_skills") val keySkills: List<KeySkillDto>?,
    val experience: ExperienceDto?,
    val employment: EmploymentDto?,
)
