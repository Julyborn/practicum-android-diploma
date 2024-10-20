package ru.practicum.android.diploma.search.domain.models

data class VacancySearchParams(
    val query: String = "",
    val location: String? = null,
    val selectedCountry: String? = null,
    val selectedRegion: String? = null,
    val industryId: String? = null,
    val salary: Int? = null,
    val hideWithoutSalary: Boolean = false,
    val perPage: Int = 20,
    val page: Int = 0,
    val area: String?
)

fun VacancySearchParams.toMap(): Map<String, String> {
    val params = mutableMapOf(
        "text" to query,
        "per_page" to perPage.toString(),
        "page" to page.toString()
    )

    industryId?.let { params["industry"] = it }
    salary?.let { params["salary"] = it.toString() }
    area?.let { params["area"] = it }

    if (hideWithoutSalary) {
        params["only_with_salary"] = "true"
    }

    return params
}
