package ru.practicum.android.diploma.filter.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.filter.domain.models.Country
import ru.practicum.android.diploma.filter.domain.models.Region
import ru.practicum.android.diploma.search.domain.models.Resource

interface WorkplaceRepository {
    suspend fun getCountries(): Resource<List<Country>>
    suspend fun getRegionsByCountry(countryId: String): Resource<List<Region>>
    fun saveSelectedCountry(country: Country?)
    fun saveSelectedRegion(region: Region?)
    fun getSelectedCountry(): Country?
    suspend fun getAllRegions(): Flow<Resource<MutableList<Region>>>
    fun getSelectedRegion(): Region?
    fun clearSavedCountry()
    fun clearSavedRegion()
}
