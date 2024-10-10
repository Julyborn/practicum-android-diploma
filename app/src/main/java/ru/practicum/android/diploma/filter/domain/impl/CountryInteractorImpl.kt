package ru.practicum.android.diploma.filter.domain.impl

import ru.practicum.android.diploma.filter.domain.api.CountryInteractor
import ru.practicum.android.diploma.filter.domain.api.CountryRepository
import ru.practicum.android.diploma.filter.domain.models.Country

class CountryInteractorImpl(private val repository: CountryRepository) : CountryInteractor {
    override suspend fun getCountries(): List<Country> {
        return repository.getCountries()
    }
}
