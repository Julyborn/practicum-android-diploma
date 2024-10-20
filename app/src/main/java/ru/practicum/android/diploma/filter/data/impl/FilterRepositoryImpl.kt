package ru.practicum.android.diploma.filter.data.impl

import ru.practicum.android.diploma.filter.data.dto.FilterSettings
import ru.practicum.android.diploma.filter.domain.api.FilterPreferences
import ru.practicum.android.diploma.filter.domain.api.FilterRepository
import ru.practicum.android.diploma.filter.domain.models.FilterParams

class FilterRepositoryImpl(
    private val filterPreferences: FilterPreferences,
) : FilterRepository {

    override fun saveFilterSettings(filterSettings: FilterSettings) {
        val filterParams = FilterParams(
            location = filterSettings.location,
            industry = filterSettings.industry,
            salary = filterSettings.salary,
            industryId = filterSettings.industryId,
            hideWithoutSalary = filterSettings.hideWithoutSalary,
            area = filterSettings.area
        )
        filterPreferences.saveFilters(filterParams)
    }

    override fun loadFilterSettings(): FilterSettings {
        return filterPreferences.getFilters()
    }

    override fun clearFilters() {
        filterPreferences.clearFilters()
    }
}
