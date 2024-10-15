package ru.practicum.android.diploma.filter.domain.impl

import ru.practicum.android.diploma.filter.data.dto.FilterSettings
import ru.practicum.android.diploma.filter.domain.api.FilterInteractor
import ru.practicum.android.diploma.filter.domain.api.FilterRepository

class FilterInteractorImpl(private val filterRepository: FilterRepository) : FilterInteractor {
    override fun saveFilterSettings(filterSettings: FilterSettings) {
        filterRepository.saveFilterSettings(filterSettings)
    }

    override fun loadFilterSettings(): FilterSettings {
        return filterRepository.loadFilterSettings()
    }
}
