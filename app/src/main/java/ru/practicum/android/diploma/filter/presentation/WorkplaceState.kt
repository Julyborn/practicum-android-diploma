package ru.practicum.android.diploma.filter.presentation

import ru.practicum.android.diploma.filter.domain.models.Country
import ru.practicum.android.diploma.filter.domain.models.Region

sealed class WorkplaceState {
    data object Loading : WorkplaceState()
    data class Success(val countries: List<Country>, val regions: List<Region>?) : WorkplaceState()
    data object NoRegionsError : WorkplaceState()
    data object FetchError : WorkplaceState()
    data object NoInternet : WorkplaceState()
}
