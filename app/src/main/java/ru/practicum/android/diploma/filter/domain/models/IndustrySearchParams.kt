package ru.practicum.android.diploma.filter.domain.models

data class IndustrySearchParams(
    val query: String,
    val industry: String? = null,
    val page: Int = 0,
    val perPage: Int = 20
)

fun IndustrySearchParams.toMap(): Map<String, String> {
    val params = mutableMapOf(
        "text" to query, "per_page" to perPage.toString(), "page" to page.toString()
    )
    return params
}
