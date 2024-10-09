package ru.practicum.android.diploma.filter.domain.api

import ru.practicum.android.diploma.filter.domain.models.Country

interface CountryInteractor {
    suspend fun getCountries(): List<Country>
}
