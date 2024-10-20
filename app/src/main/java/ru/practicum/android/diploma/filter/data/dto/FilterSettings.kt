package ru.practicum.android.diploma.filter.data.dto

data class FilterSettings(
    var location: String? = null,
    var industry: String? = null,
    var salary: String? = null,
    var industryId: String? = null,
    var selectedCountry: String? = null,
    var selectedRegion: String? = null,
    var hideWithoutSalary: Boolean = false
)
