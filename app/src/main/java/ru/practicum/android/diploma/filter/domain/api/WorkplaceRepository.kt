package ru.practicum.android.diploma.filter.domain.api

import ru.practicum.android.diploma.filter.domain.models.Country
import ru.practicum.android.diploma.filter.domain.models.Region

interface WorkplaceRepository {
    suspend fun getCountries(): List<Country>
    suspend fun getRegionsByCountry(countryId: String): List<Region>
    fun saveSelectedCountry(country: Country?)
    fun saveSelectedRegion(region: Region?)
    fun getSelectedCountry(): Country?
    suspend fun getAllRegions(): List<Region>
    fun getSelectedRegion(): Region?
    fun clearSavedCountry()
    fun clearSavedRegion()
}
