package ru.practicum.android.diploma.filter.data.dto

data class FilterSettings(
    val location: String?,
    var industry: String?,
    val salary: String?,
    val hideWithoutSalary: Boolean
)
