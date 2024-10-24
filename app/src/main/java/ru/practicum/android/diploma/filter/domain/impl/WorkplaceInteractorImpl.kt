package ru.practicum.android.diploma.filter.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.filter.domain.api.WorkplaceInteractor
import ru.practicum.android.diploma.filter.domain.api.WorkplaceRepository
import ru.practicum.android.diploma.filter.domain.models.Country
import ru.practicum.android.diploma.filter.domain.models.Region
import ru.practicum.android.diploma.search.domain.models.Resource

class WorkplaceInteractorImpl(private val repository: WorkplaceRepository) : WorkplaceInteractor {
    override suspend fun getCountries(): Resource<List<Country>> {
        return repository.getCountries()
    }

    override suspend fun getRegionsByCountry(countryId: String): Resource<List<Region>> {
        return repository.getRegionsByCountry(countryId)
    }

    override fun saveSelectedCountry(country: Country?) {
        repository.saveSelectedCountry(country)
    }

    override fun saveSelectedRegion(region: Region?) {
        repository.saveSelectedRegion(region)
    }

    override fun getSelectedCountry(): Country? {
        return repository.getSelectedCountry()
    }

    override suspend fun getAllRegions(): Flow<Resource<MutableList<Region>>> {
        return repository.getAllRegions()
    }

    override fun getSelectedRegion(): Region? {
        return repository.getSelectedRegion()
    }

    override fun clearSavedCountry() {
        repository.clearSavedCountry()
    }

    override fun clearSavedRegion() {
        repository.clearSavedRegion()
    }
}
