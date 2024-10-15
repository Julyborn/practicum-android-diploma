package ru.practicum.android.diploma.filter.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.filter.data.dto.IndustryDto
import ru.practicum.android.diploma.filter.domain.models.IndustrySearchParams
import ru.practicum.android.diploma.search.domain.models.Resource

interface IndustryRepository {
    fun searchIndustries(params: IndustrySearchParams): Flow<Resource<List<IndustryDto>>>
}
