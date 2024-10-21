package ru.practicum.android.diploma.filter.data.impl

import android.content.Context
import android.net.http.HttpException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.practicum.android.diploma.filter.data.dto.IndustryDto
import ru.practicum.android.diploma.filter.data.dto.IndustryResponse
import ru.practicum.android.diploma.filter.domain.api.IndustryRepository
import ru.practicum.android.diploma.filter.domain.models.IndustrySearchParams
import ru.practicum.android.diploma.filter.domain.models.toMap
import ru.practicum.android.diploma.search.data.network.HeadHunterAPI
import ru.practicum.android.diploma.search.data.network.NetworkUtils
import ru.practicum.android.diploma.search.domain.models.Resource
import java.io.IOException

class IndustryRepositoryImp(
    private val api: HeadHunterAPI,
    private val context: Context
) : IndustryRepository {

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun searchIndustries(params: IndustrySearchParams): Flow<Resource<List<IndustryDto>>> = flow {
        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val options = params.toMap()
                val response: List<IndustryResponse> = api.getIndustries(options)
                val industries = response.flatMap { industryResponse ->
                    industryResponse.industries.map { industry ->
                        IndustryDto(
                            id = industry.id,
                            name = industry.name
                        )
                    }
                }

                val sortedIndustries = industries.sortedBy { it.name.lowercase() }

                Log.d("IndustryRepositoryImp", "sortedIndustries $sortedIndustries")

                if (sortedIndustries.isNotEmpty()) {
                    emit(Resource.Success(data = sortedIndustries, found = sortedIndustries.size, page = 0, pages = 1))
                } else {
                    emit(
                        Resource.Success(
                            data = emptyList(),
                            found = 0,
                            page = 0,
                            pages = 0
                        )
                    )
                }
            } catch (e: IOException) {
                emit(Resource.ServerError("Network error: ${e.localizedMessage}"))
            } catch (e: HttpException) {
                emit(Resource.ServerError("HTTP error: ${e.localizedMessage}"))
            }
        } else {
            emit(Resource.NoInternetError("Network not available"))
        }
    }.flowOn(Dispatchers.IO)
}
