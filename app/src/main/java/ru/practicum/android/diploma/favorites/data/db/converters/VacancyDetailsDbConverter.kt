package ru.practicum.android.diploma.favorites.data.db.converters

import com.google.gson.Gson
import ru.practicum.android.diploma.favorites.data.db.entity.VacancyDetailsEntity
import ru.practicum.android.diploma.search.domain.models.Employer
import ru.practicum.android.diploma.search.domain.models.Salary
import ru.practicum.android.diploma.vacancies.domain.models.VacancyDetails

class VacancyDetailsDbConverter(private val gson: Gson) {
    fun map(vacancyDetails: VacancyDetails): VacancyDetailsEntity {
        return VacancyDetailsEntity(
            id = vacancyDetails.id,
            name = vacancyDetails.name,
            salaryJson = serializeSalary(vacancyDetails.salary),
            employerJson = serializeEmployer(vacancyDetails.employer),
            address = vacancyDetails.address,
            areaName = vacancyDetails.areaName,
            description = vacancyDetails.description,
            skills = vacancyDetails.skills,
            experience = vacancyDetails.experience,
            employment = vacancyDetails.employment
        )
    }

    fun map(vacancyDetailsEntity: VacancyDetailsEntity): VacancyDetails {
        return VacancyDetails(
            id = vacancyDetailsEntity.id,
            name = vacancyDetailsEntity.name,
            salary = deserializeSalary(vacancyDetailsEntity.salaryJson),
            employer = deserializeEmployer(vacancyDetailsEntity.employerJson),
            address = vacancyDetailsEntity.address,
            areaName = vacancyDetailsEntity.areaName,
            description = vacancyDetailsEntity.description,
            skills = vacancyDetailsEntity.skills,
            experience = vacancyDetailsEntity.experience,
            employment = vacancyDetailsEntity.employment
        )
    }

    private fun serializeSalary(salary: Salary?): String? {
        return salary?.let { gson.toJson(it) }
    }

    private fun deserializeSalary(json: String?): Salary? {
        return json?.let { gson.fromJson(it, Salary::class.java) }
    }

    private fun serializeEmployer(employer: Employer?): String? {
        return employer?.let { gson.toJson(it) }
    }

    private fun deserializeEmployer(json: String?): Employer? {
        return json?.let { gson.fromJson(it, Employer::class.java) }
    }
}
