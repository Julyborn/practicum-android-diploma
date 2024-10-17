package ru.practicum.android.diploma.filter.domain.models

data class Region(
    val id: String,
    val name: String,
    val parentId: String? = null
)
