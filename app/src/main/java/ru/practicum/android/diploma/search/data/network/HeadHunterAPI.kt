package ru.practicum.android.diploma.search.data.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.QueryMap
import ru.practicum.android.diploma.search.data.dto.Industry
import ru.practicum.android.diploma.search.data.dto.SearchResponse
import ru.practicum.android.diploma.vacancies.data.dto.VacancyDetailsDto

interface HeadHunterAPI {
    // Поиск вакансий
    @GET("vacancies")
    suspend fun searchVacancies(
        @QueryMap options: Map<String, String>
    ): SearchResponse

    // Получение списка всех отраслей
    @GET("industries")
    suspend fun getIndustries(): List<Industry>

    @GET("vacancies/{id}")
    suspend fun getVacancyDetails(
        @Header("User-Agent") userAgent: String?,
        @Path("id") id: String
    ): VacancyDetailsDto
}
