package ru.practicum.android.diploma.filter.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.filter.data.dto.FilterSettings
import ru.practicum.android.diploma.filter.domain.api.FilterInteractor

class FilterViewModel(private val filterInteractor: FilterInteractor) : ViewModel() {

    fun loadFilters(): FilterSettings {
        return filterInteractor.loadFilterSettings()
    }

    fun applyFilters(
        location: String?,
        industry: String?,
        salary: String?,
        industryId: String?,
        selectedCountry: String?,
        selectedRegion: String?,
        hideWithoutSalary: Boolean
    ) {
        val filterSettings = FilterSettings(location, industry, salary, industryId, selectedCountry, selectedRegion, hideWithoutSalary)

        filterInteractor.saveFilterSettings(filterSettings)
    }

    fun clearFilters() {
        filterInteractor.clearFilterSettings()
    }
}
