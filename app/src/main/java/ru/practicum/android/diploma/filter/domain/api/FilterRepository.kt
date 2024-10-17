package ru.practicum.android.diploma.filter.domain.api

import ru.practicum.android.diploma.filter.data.dto.FilterSettings

interface FilterRepository {
    fun saveFilterSettings(filterSettings: FilterSettings)
    fun loadFilterSettings(): FilterSettings
    fun clearFilters()
}
