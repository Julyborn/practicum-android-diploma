package ru.practicum.android.diploma.filter.data.dto

data class FilterSettings(
    val location: String?,
    val industry: String?,
    val salary: String?,
    val hideWithoutSalary: Boolean
)
