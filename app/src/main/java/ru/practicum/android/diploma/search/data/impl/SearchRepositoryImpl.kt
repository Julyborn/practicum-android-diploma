package ru.practicum.android.diploma.search.data.impl

import android.content.Context
import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.practicum.android.diploma.search.data.dto.AreaDto
import ru.practicum.android.diploma.search.data.dto.EmployerDto
import ru.practicum.android.diploma.search.data.dto.SalaryDto
import ru.practicum.android.diploma.search.data.network.HeadHunterAPI
import ru.practicum.android.diploma.search.data.network.NetworkUtils
import ru.practicum.android.diploma.search.domain.api.SearchRepository
import ru.practicum.android.diploma.search.domain.models.Employer
import ru.practicum.android.diploma.search.domain.models.Resource
import ru.practicum.android.diploma.search.domain.models.Salary
import ru.practicum.android.diploma.search.domain.models.Vacancy
import ru.practicum.android.diploma.search.domain.models.VacancySearchParams
import ru.practicum.android.diploma.search.domain.models.toMap
import java.io.IOException

class SearchRepositoryImpl(
    private val api: HeadHunterAPI,
    private val context: Context
) : SearchRepository {

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun searchVacancies(params: VacancySearchParams): Flow<Resource<List<Vacancy>>> = flow {
        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val options = params.toMap()

                val response = api.searchVacancies(options)

                val vacancies = response.items.map { vacancy ->
                    Vacancy(
                        id = vacancy.id,
                        name = vacancy.name,
                        salary = getSalary(vacancy.salary),
                        areaName = getArea(vacancy.area),
                        industryId = vacancy.industryId?.id ?: "",
                        selectedCountry = vacancy.selectedCountry ?: "",
                        selectedRegion  =   vacancy.selectedRegion ?: "",
                        employer = getEmployer(vacancy.employer)
                    )
                }.filter { vacancy ->

                    val matchesLocation = params.selectedCountry?.let { selectedCountry ->
                        val matchesCountry = vacancy.areaName.contains(selectedCountry, ignoreCase = true)
                        val matchesRegion = params.selectedRegion?.let { selectedRegion ->
                            vacancy.areaName.contains(selectedRegion, ignoreCase = true)
                        } ?: true

                        matchesCountry || matchesRegion
                    } ?: true

                    val matchesIndustry = params.industryId?.let { industryId ->
                        vacancy.industryId == industryId
                    } ?: true

                    val salary = vacancy.salary.from ?: 0
                    val matchesSalary = params.salary?.let { salary >= it } ?: true

                    val matchesHideWithoutSalary = if (params.hideWithoutSalary) {
                        vacancy.salary.from != null || vacancy.salary.to != null
                    } else {
                        true
                    }

                    matchesLocation && matchesIndustry && matchesSalary && matchesHideWithoutSalary
                }
                emit(
                    Resource.Success(
                        data = vacancies,
                        found = response.found,
                        page = response.page,
                        pages = response.pages
                    )
                )

            } catch (e: IOException) {
                emit(Resource.ServerError("Network error: ${e.localizedMessage}"))
            } catch (e: HttpException) {
                emit(Resource.ServerError("HTTP error: ${e.localizedMessage}"))
            }
        } else {
            emit(Resource.NoInternetError("Network not available"))
        }
    }.flowOn(Dispatchers.IO)

    private fun getSalary(salaryDto: SalaryDto?): Salary {
        if (salaryDto == null) {
            return Salary()
        }
        return Salary(
            currency = salaryDto.currency,
            from = salaryDto.from,
            gross = salaryDto.gross,
            to = salaryDto.to
        )
    }

    private fun getArea(areaDto: AreaDto?): String =
        areaDto?.name.orEmpty()

    private fun getEmployer(employerDto: EmployerDto?): Employer {
        if (employerDto == null) {
            return Employer()
        }
        return Employer(
            id = employerDto.id,
            name = employerDto.name,
            logoUrl = employerDto.logoUrls?.size90
        )
    }
}
