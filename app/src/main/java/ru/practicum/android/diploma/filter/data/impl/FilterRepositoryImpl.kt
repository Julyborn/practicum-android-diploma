package ru.practicum.android.diploma.filter.data.impl

import ru.practicum.android.diploma.filter.data.dto.FilterSettings
import ru.practicum.android.diploma.filter.domain.api.FilterPreferences
import ru.practicum.android.diploma.filter.domain.api.FilterRepository
import ru.practicum.android.diploma.filter.domain.api.WorkplaceRepository

class FilterRepositoryImpl(private val filterPreferences: FilterPreferences, private val workplaceRepository: WorkplaceRepository) : FilterRepository {

    override fun saveFilterSettings(filterSettings: FilterSettings) {
        val selectedCountry = workplaceRepository.getSelectedCountry()
        val selectedRegion = workplaceRepository.getSelectedRegion()

        filterPreferences.saveFilters(
            filterSettings.location,
            filterSettings.industry,
            filterSettings.salary,
            filterSettings.industryId,
            filterSettings.hideWithoutSalary,
            selectedCountry?.id,
            selectedRegion?.id
        )
    }

    override fun loadFilterSettings(): FilterSettings {
        return filterPreferences.getFilters()
    }

    override fun clearFilters() {
        filterPreferences.clearFilters()
    }
}
