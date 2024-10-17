package ru.practicum.android.diploma.filter.domain.models

data class FilterParams(
    val location: String? = null,
    val industry: String? = null,
    val salary: String? = null,
    val industryId: String? = null,
    val hideWithoutSalary: Boolean = false,
    val area: String? = null
)
