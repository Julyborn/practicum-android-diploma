package ru.practicum.android.diploma.filter.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.filter.data.dto.IndustryDto
import ru.practicum.android.diploma.filter.domain.api.IndustryInteractor
import ru.practicum.android.diploma.filter.domain.api.IndustryRepository
import ru.practicum.android.diploma.filter.domain.models.IndustrySearchParams
import ru.practicum.android.diploma.search.domain.models.Resource

class IndustryInteractorImpl(private val industryRepository: IndustryRepository) : IndustryInteractor {
    override suspend fun searchIndustries(params: IndustrySearchParams): Flow<Resource<List<IndustryDto>>> {
        return industryRepository.searchIndustries(params)
    }
}
