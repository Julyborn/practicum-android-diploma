package ru.practicum.android.diploma.filter.data.impl

import android.content.SharedPreferences
import ru.practicum.android.diploma.filter.domain.api.WorkplaceRepository
import ru.practicum.android.diploma.filter.domain.models.Country
import ru.practicum.android.diploma.filter.domain.models.Region
import ru.practicum.android.diploma.search.data.network.HeadHunterAPI
import java.io.IOException

class WorkplaceRepositoryImpl(
    private val api: HeadHunterAPI,
    private val sharedPreferences: SharedPreferences
) : WorkplaceRepository {
    override suspend fun getCountries(): List<Country> {
        return try {
            api.getAreas().filter { it.parentId == null }.map { areaDto ->
                Country(
                    id = areaDto.id,
                    name = areaDto.name.toString()
                )
            }
        } catch (e: IOException) {
            emptyList()
        }
    }

    override suspend fun getRegionsByCountry(countryId: String): List<Region> {
        return try {
            val response = api.getRegions(countryId)
            response.areas.map { areaDto ->
                Region(
                    id = areaDto.id,
                    name = areaDto.name ?: DEF,
                    parentId = areaDto.parentId ?: DEF
                )
            }
        } catch (e: IOException) {
            emptyList()
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

    override suspend fun getAllRegions(): List<Region> {
        val countries = getCountries()
        val regionsList = mutableListOf<Region>()
        countries.forEach { country ->
            val regionsByCountry = getRegionsByCountry(country.id)
            regionsList.addAll(regionsByCountry)
        }
        return regionsList
    }

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
