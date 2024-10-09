package ru.practicum.android.diploma.filter.data.impl

import ru.practicum.android.diploma.filter.domain.api.CountryRepository
import ru.practicum.android.diploma.filter.domain.models.Country
import ru.practicum.android.diploma.search.data.network.HeadHunterAPI

class CountryRepositoryImpl(
    private val api: HeadHunterAPI
) : CountryRepository {
    override suspend fun getCountries(): List<Country> {
        return api.getCountries().filter { it.parentId == null }.map { areaDto ->
            Country(
                id = areaDto.id,
                name = areaDto.name.toString()
            )
        }
    }
}
