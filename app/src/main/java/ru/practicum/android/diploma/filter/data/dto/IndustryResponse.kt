package ru.practicum.android.diploma.filter.data.dto

data class IndustryResponse(
    val id: String,
    val name: String,
    val parent_id: String?,
    val industries: List<IndustryDto> = emptyList() // Вложенные отрасли
)
