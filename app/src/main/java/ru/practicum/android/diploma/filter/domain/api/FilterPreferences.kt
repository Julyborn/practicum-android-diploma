package ru.practicum.android.diploma.filter.domain.api

import ru.practicum.android.diploma.filter.data.dto.FilterSettings
import ru.practicum.android.diploma.filter.domain.models.FilterParams

interface FilterPreferences {
    fun saveFilters(params: FilterParams)
    fun getFilters(): FilterSettings
    fun clearFilters()
}
