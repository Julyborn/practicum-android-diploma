package ru.practicum.android.diploma.filter.domain.api

import ru.practicum.android.diploma.filter.domain.models.Country

interface CountryRepository {
    suspend fun getCountries(): List<Country>
}
