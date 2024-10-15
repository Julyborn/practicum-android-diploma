package ru.practicum.android.diploma.filter.domain.api

import android.content.Context
import ru.practicum.android.diploma.filter.data.dto.FilterSettings

class FilterPreferences(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("filter_prefs", Context.MODE_PRIVATE)

    fun saveFilters(location: String?, industry: String?, salary: String?, hideWithoutSalary: Boolean) {
        sharedPreferences.edit().apply {
            putString("location", location)
            putString("industry", industry)
            putString("salary", salary)
            putBoolean("hideWithoutSalary", hideWithoutSalary)
            apply()
        }
    }

    fun getFilters(): FilterSettings {
        val location = sharedPreferences.getString("location", null)
        val industry = sharedPreferences.getString("industry", null)
        val salary = sharedPreferences.getString("salary", null)
        val hideWithoutSalary = sharedPreferences.getBoolean("hideWithoutSalary", false)
        return FilterSettings(location, industry, salary, hideWithoutSalary)
    }
}

