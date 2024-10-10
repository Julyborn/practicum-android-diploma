package ru.practicum.android.diploma.filter.presentation

import ru.practicum.android.diploma.filter.domain.models.Country

sealed class CountryState {
    data class Success(val countries: List<Country>) : CountryState()
    data object Error : CountryState()
}
