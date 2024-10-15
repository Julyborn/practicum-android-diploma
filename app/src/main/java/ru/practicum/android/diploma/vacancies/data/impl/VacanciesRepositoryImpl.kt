package ru.practicum.android.diploma.vacancies.data.impl

import ru.practicum.android.diploma.search.data.network.HeadHunterAPI
import ru.practicum.android.diploma.search.domain.models.Employer
import ru.practicum.android.diploma.search.domain.models.Salary
import ru.practicum.android.diploma.vacancies.data.dto.VacancyDetailsDto
import ru.practicum.android.diploma.vacancies.domain.api.VacanciesRepository
import ru.practicum.android.diploma.vacancies.domain.models.VacancyDetails

class VacanciesRepositoryImpl(private val api: HeadHunterAPI) : VacanciesRepository {
    override suspend fun getVacancyDetails(vacancyId: String): VacancyDetails {
        val vacancyDetailsDto = api.getVacancyDetails(
            userAgent = "Android",
            id = vacancyId
        )
        return mapToDomain(vacancyDetailsDto)
    }

    private fun mapToDomain(vacancyDto: VacancyDetailsDto): VacancyDetails {
        return VacancyDetails(
            id = vacancyDto.id,
            name = vacancyDto.name,
            salary = vacancyDto.salary?.let {
                Salary(
                    currency = it.currency,
                    from = it.from,
                    to = it.to,
                    gross = it.gross
                )
            },
            employer = vacancyDto.employer?.let {
                Employer(
                    id = it.id,
                    name = it.name,
                    logoUrl = it.logoUrls?.size90
                )
            },
            address = vacancyDto.address?.let { addressDto ->
                listOfNotNull(
                    addressDto.city,
                    addressDto.street,
                    addressDto.building,
                ).joinToString(", ")
            },
            areaName = vacancyDto.area?.name,
            description = vacancyDto.description,
            skills = vacancyDto.keySkills?.joinToString("<br />") { " • ${it.name}" },
            experience = vacancyDto.experience?.name,
            employment = vacancyDto.employment?.name
        )
    }
}
