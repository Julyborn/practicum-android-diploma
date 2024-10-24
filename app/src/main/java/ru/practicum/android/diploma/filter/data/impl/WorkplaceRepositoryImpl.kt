package ru.practicum.android.diploma.filter.data.impl

import android.content.Context
import android.content.SharedPreferences
import android.net.http.HttpException
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
            } catch (e: HttpException) {
                Resource.ServerError("HTTP error: ${e.localizedMessage}")
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
                    Country(id = areaDto.id, name = areaDto.name ?: DEF)
                }
        }
    }

    override suspend fun getRegionsByCountry(countryId: String): Resource<List<Region>> {
        return safeNetworkCall {
            api.getRegions(countryId).areas.map { areaDto ->
                Region(
                    id = areaDto.id,
                    name = areaDto.name ?: DEF,
                    parentId = areaDto.parentId ?: DEF
                )
            }
        }
    }

    private fun putString(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    private fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    override fun saveSelectedCountry(country: Country?) {
        putString(COUNTRY_ID, country?.id)
        putString(COUNTRY_NAME, country?.name)
    }

    override fun saveSelectedRegion(region: Region?) {
        putString(REGION_ID, region?.id)
        putString(REGION_NAME, region?.name)
        putString(PARENT_ID, region?.parentId)
    }

    override fun getSelectedCountry(): Country? {
        val id = getString(COUNTRY_ID)
        val name = getString(COUNTRY_NAME)
        return if (id != null && name != null) {
            Country(id, name)
        } else {
            null
        }
    }

    override fun getSelectedRegion(): Region? {
        val id = getString(REGION_ID)
        val name = getString(REGION_NAME)
        val parentId = getString(PARENT_ID)
        return if (id != null && name != null) {
            Region(id, name, parentId)
        } else {
            null
        }
    }

    override suspend fun getAllRegions(): Flow<Resource<MutableList<Region>>> = flow {
        emit(safeNetworkCall { fetchAllRegions() })
    }.flowOn(Dispatchers.IO)

    private suspend fun fetchAllRegions(): MutableList<Region> {
        val countries = fetchCountries()
        if (countries.isEmpty()) {
            throw IOException("No countries found")
        }

        val regionsList = mutableListOf<Region>()
        countries.forEach { country ->
            regionsList.addAll(fetchRegionsForCountry(country.id))
        }

        if (regionsList.isEmpty()) {
            throw IOException("No regions found")
        }

        return regionsList
    }

    private suspend fun fetchCountries(): List<Country> {
        return api.getAreas()
            .filter { it.parentId == null }
            .map { areaDto ->
                Country(
                    id = areaDto.id,
                    name = areaDto.name ?: DEF
                )
            }
    }

    private suspend fun fetchRegionsForCountry(countryId: String): List<Region> {
        return api.getRegions(countryId).areas.map { areaDto ->
            Region(
                id = areaDto.id,
                name = areaDto.name ?: DEF,
                parentId = areaDto.parentId ?: DEF
            )
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
