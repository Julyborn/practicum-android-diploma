package ru.practicum.android.diploma.search.data.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.QueryMap
import ru.practicum.android.diploma.filter.data.dto.IndustryResponse
import ru.practicum.android.diploma.filter.data.dto.AreaResponseDto
import ru.practicum.android.diploma.search.data.dto.AreaDto
import ru.practicum.android.diploma.search.data.dto.Industry
import ru.practicum.android.diploma.search.data.dto.SearchResponse
import ru.practicum.android.diploma.vacancies.data.dto.VacancyDetailsDto

interface HeadHunterAPI {
    // Поиск вакансий
    @GET("vacancies")
    suspend fun searchVacancies(
        @QueryMap options: Map<String, String>
    ): SearchResponse

    @GET("industries")
    suspend fun getIndustries(
        @QueryMap options: Map<String, String>
    ): List<IndustryResponse>

    @GET("vacancies/{id}")
    suspend fun getVacancyDetails(
        @Header("User-Agent") userAgent: String?,
        @Path("id") id: String
    ): VacancyDetailsDto

    @GET("areas")
    suspend fun getAreas(): List<AreaDto>

    @GET("areas/{area_id}")
    suspend fun getRegions(
        @Path("area_id") areaId: String
    ): AreaResponseDto
}

