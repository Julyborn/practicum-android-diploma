package ru.practicum.android.diploma.filter.domain.api

import ru.practicum.android.diploma.filter.data.dto.FilterSettings

interface FilterPreferences {
    fun saveFilters(location: String?, industry: String?, salary: String?, industryId: String?, hideWithoutSalary: Boolean)
    fun getFilters(): FilterSettings
    fun clearFilters()
}
