package ru.practicum.android.diploma.filter.data.impl

import ru.practicum.android.diploma.filter.data.dto.FilterSettings
import ru.practicum.android.diploma.filter.domain.api.FilterPreferences
import ru.practicum.android.diploma.filter.domain.api.FilterRepository

class FilterRepositoryImpl(private val filterPreferences: FilterPreferences) : FilterRepository {

    override fun saveFilterSettings(filterSettings: FilterSettings) {
        filterPreferences.saveFilters(
            filterSettings.location,
            filterSettings.industry,
            filterSettings.salary,
            filterSettings.hideWithoutSalary
        )
    }

    override fun loadFilterSettings(): FilterSettings {
        return filterPreferences.getFilters()
    }

    override fun clearFilters() {
        filterPreferences.clearFilters()
    }
}
