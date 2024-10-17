package ru.practicum.android.diploma.filter.data.impl

import android.content.Context
import android.content.SharedPreferences
import ru.practicum.android.diploma.filter.data.dto.FilterSettings
import ru.practicum.android.diploma.filter.domain.api.FilterPreferences

class FilterPreferencesImpl(context: Context) : FilterPreferences {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("filter_prefs", Context.MODE_PRIVATE)

    override fun saveFilters(
        location: String?,
        industry: String?,
        salary: String?,
        industryId: String?,
        hideWithoutSalary: Boolean,
        countryId: String?,
        regionId: String?
    ) {
        sharedPreferences.edit().apply {
            putString(KEY_LOCATION, location)
            putString(KEY_INDUSTRY, industry)
            putString(KEY_SALARY, salary)
            putString(KEY_INDUSTRY_ID, industryId)
            putBoolean(KEY_HIDE_WITHOUT_SALARY, hideWithoutSalary)
            putString(KEY_SELECTED_COUNTRY, countryId)
            putString(KEY_SELECTED_REGION, regionId)
            apply()
        }
    }

    override fun getFilters(): FilterSettings {
        val location = sharedPreferences.getString(KEY_LOCATION, null)
        val industry = sharedPreferences.getString(KEY_INDUSTRY, null)
        val salary = sharedPreferences.getString(KEY_SALARY, null)
        val industryId = sharedPreferences.getString(KEY_INDUSTRY_ID, null)
        val selectedCountry = sharedPreferences.getString(KEY_SELECTED_COUNTRY, null)
        val selectedRegion = sharedPreferences.getString(KEY_SELECTED_REGION, null)
        val hideWithoutSalary = sharedPreferences.getBoolean(KEY_HIDE_WITHOUT_SALARY, false)
        return FilterSettings(
            location,
            industry,
            salary,
            industryId,
            selectedCountry,
            selectedRegion,
            hideWithoutSalary
        )
    }

    override fun clearFilters() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        private const val KEY_LOCATION = "key_location"
        private const val KEY_INDUSTRY = "key_industry"
        private const val KEY_SALARY = "key_salary"
        private const val KEY_INDUSTRY_ID = "key_industry_id"
        private const val KEY_SELECTED_COUNTRY = "key_selected_country"
        private const val KEY_SELECTED_REGION = "key_selected_region"
        private const val KEY_HIDE_WITHOUT_SALARY = "key_hide_without_salary"
    }
}
