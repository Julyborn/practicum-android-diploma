package ru.practicum.android.diploma.filter.data.impl

import android.content.Context
import android.content.SharedPreferences
import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.practicum.android.diploma.filter.domain.api.WorkplaceRepository
import ru.practicum.android.diploma.filter.domain.models.Country
import ru.practicum.android.diploma.filter.domain.models.Region
import ru.practicum.android.diploma.search.data.network.HeadHunterAPI
import ru.practicum.android.diploma.search.data.network.NetworkUtils
import ru.practicum.android.diploma.search.domain.models.Resource
import java.io.IOException

class WorkplaceRepositoryImpl(
    private val api: HeadHunterAPI,
    private val sharedPreferences: SharedPreferences,
    private val context: Context
) : WorkplaceRepository {

    private suspend fun <T> safeNetworkCall(
        networkCall: suspend () -> T
    ): Resource<T> {
        return if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                Resource.Success(networkCall(), found = null, page = null, pages = null)
            } catch (e: IOException) {
                Resource.ServerError(e.message ?: "Server error")
            }
        } else {
            Resource.NoInternetError("No internet connection")
        }
    }

    override suspend fun getCountries(): Resource<List<Country>> {
        return safeNetworkCall {
            api.getAreas()
                .filter { it.parentId == null }
                .map { areaDto ->
                    Country(
                        id = areaDto.id,
                        name = areaDto.name.toString()
                    )
                }
        }
    }

    override suspend fun getRegionsByCountry(countryId: String): Resource<List<Region>> {
        return safeNetworkCall {
            val response = api.getRegions(countryId)
            response.areas.map { areaDto ->
                Region(
                    id = areaDto.id,
                    name = areaDto.name ?: DEF,
                    parentId = areaDto.parentId ?: DEF
                )
            }
        }
    }

    override fun saveSelectedCountry(country: Country?) {
        sharedPreferences.edit()
            .putString(COUNTRY_ID, country?.id)
            .putString(COUNTRY_NAME, country?.name)
            .apply()
    }

    override fun saveSelectedRegion(region: Region?) {
        sharedPreferences.edit()
            .putString(REGION_ID, region?.id)
            .putString(REGION_NAME, region?.name)
            .putString(PARENT_ID, region?.parentId)
            .apply()
    }

    override fun getSelectedCountry(): Country? {
        val id = sharedPreferences.getString(COUNTRY_ID, null)
        val name = sharedPreferences.getString(COUNTRY_NAME, null)
        return if (id != null && name != null) {
            Country(id, name)
        } else {
            null
        }
    }

    override suspend fun getAllRegions(): Flow<Resource<MutableList<Region>>> = flow {
        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val countriesResponse = api.getAreas()
                val countries = countriesResponse.filter { it.parentId == null }
                    .map { areaDto ->
                        Country(
                            id = areaDto.id,
                            name = areaDto.name.toString()
                        )
                    }

                if (countries.isEmpty()) {
                    emit(Resource.ServerError("No countries found"))
                    return@flow
                }

                val regionsList = mutableListOf<Region>()
                countries.forEach { country ->
                    val regionsByCountryResponse = api.getRegions(country.id)
                    val regionsByCountry = regionsByCountryResponse.areas.map { areaDto ->
                        Region(
                            id = areaDto.id,
                            name = areaDto.name ?: DEF,
                            parentId = areaDto.parentId ?: DEF
                        )
                    }
                    regionsList.addAll(regionsByCountry)
                }

                if (regionsList.isNotEmpty()) {
                    emit(Resource.Success(data = regionsList, found = regionsList.size, page = null, pages = null))
                } else {
                    emit(Resource.ServerError("No regions found"))
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

    override fun getSelectedRegion(): Region? {
        val id = sharedPreferences.getString(REGION_ID, null)
        val name = sharedPreferences.getString(REGION_NAME, null)
        val parentId = sharedPreferences.getString(PARENT_ID, null)

        return if (id != null && name != null) {
            Region(id, name, parentId)
        } else {
            null
        }
    }

    override fun clearSavedCountry() {
        sharedPreferences.edit()
            .remove(COUNTRY_ID)
            .remove(COUNTRY_NAME)
            .apply()
    }

    override fun clearSavedRegion() {
        sharedPreferences.edit()
            .remove(REGION_ID)
            .remove(REGION_NAME)
            .remove(PARENT_ID)
            .apply()
    }

    companion object {
        const val COUNTRY_ID = "selected_country_id"
        const val COUNTRY_NAME = "selected_country_name"
        const val REGION_ID = "selected_region_id"
        const val REGION_NAME = "selected_region_name"
        const val PARENT_ID = "parent_country_id"
        const val DEF = ""
    }
}
