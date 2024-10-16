package ru.practicum.android.diploma.filter.data.dto

data class FilterSettings(
    var location: String?,
    var industry: String?,
    var salary: String?,
    var industryId: String?,
    var selectedCountry: String?,
    var selectedRegion: String?,
    var hideWithoutSalary: Boolean
)
