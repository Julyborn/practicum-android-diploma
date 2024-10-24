package ru.practicum.android.diploma.search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filter.domain.api.FilterInteractor
import ru.practicum.android.diploma.search.domain.api.SearchInteractor
import ru.practicum.android.diploma.search.domain.models.Resource
import ru.practicum.android.diploma.search.domain.models.Vacancy
import ru.practicum.android.diploma.search.domain.models.VacancySearchParams
import ru.practicum.android.diploma.search.presentation.models.UiScreenState
import ru.practicum.android.diploma.search.presentation.models.VacancyUi
import ru.practicum.android.diploma.util.formatSalary

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val filterInteractor: FilterInteractor
) : ViewModel() {
    private val _uiState = MutableLiveData<UiScreenState>(UiScreenState.Default)
    val uiState: LiveData<UiScreenState>
        get() = _uiState

    private val _vacanciesList = MutableLiveData<List<VacancyUi>>(emptyList())
    val vacanciesList: LiveData<List<VacancyUi>>
        get() = _vacanciesList

    private val _errorEvent = MutableLiveData<String>()
    val errorEvent: LiveData<String>
        get() = _errorEvent

    private val _searchQuery = MutableLiveData<String>("")

    private var searchJob: Job? = null

    // Переменные для фильтра
    private var filterLocation: String? = null

    //    private var filterIndustry: String? = null
    private var filterSalary: String? = null
    private var hideWithoutSalary: Boolean = false
    private var filterIndustryId: String? = null
    private var area: String? = null

    // Переменные пагинации
    private var currentPage = 0
    private var maxPages = Int.MAX_VALUE
    var isFirstSearch = false
    private val _isNextPageLoading = MutableLiveData<Boolean>(false)
    val isNextPageLoading: LiveData<Boolean>
        get() = _isNextPageLoading
    private var totalVacanciesFound: Int = 0

    init {
        filterInteractor.loadFilterSettings()
    }

    private fun loadSavedFilters() {
        val savedFilters = filterInteractor.loadFilterSettings()
        filterLocation = savedFilters.location
        // filterIndustry = savedFilters.industry
        filterSalary = savedFilters.salary
        hideWithoutSalary = savedFilters.hideWithoutSalary
        filterIndustryId = savedFilters.industryId
        area = savedFilters.area
    }

    fun onSearchQueryChanged(query: String) {
        if (_searchQuery.value == query && !isFiltersChanged()) {
            return
        }
        currentPage = 0
        maxPages = Int.MAX_VALUE
        _vacanciesList.value = emptyList()
        _searchQuery.value = query
        isFirstSearch = true

        loadSavedFilters()

        if (query.isEmpty()) {
            searchJob?.cancel()
            _uiState.value = UiScreenState.Default
            return
        }
        val params = buildSearchParams(query)
        _uiState.value = UiScreenState.Loading
        searchRequest(params)
    }

    fun applyFilters(location: String?, industry: String?, salary: String?, hideWithoutSalary: Boolean) {
        filterLocation = location
        // filterIndustry = industry
        filterSalary = salary
        this.hideWithoutSalary = hideWithoutSalary

        val query = _searchQuery.value ?: return
        onSearchQueryChanged(query)
    }

    fun onLastItemReached() {
        if (_isNextPageLoading.value == true || currentPage >= maxPages - 1) return
        _isNextPageLoading.value = true
        val query = _searchQuery.value ?: return
        val params = buildSearchParams(query = query, page = currentPage + 1)
        searchRequest(params)
    }

    private fun buildSearchParams(query: String, page: Int = 0): VacancySearchParams {
        return VacancySearchParams(
            query = query,
            location = filterLocation,
            industryId = filterIndustryId,
            salary = filterSalary?.toIntOrNull(),
            hideWithoutSalary = hideWithoutSalary,
            page = page,
            area = area
        )
    }

    private fun searchRequest(params: VacancySearchParams) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            searchInteractor.searchVacancies(params).collect { result ->
                renderState(result)
                _isNextPageLoading.value = false
            }
        }
    }

    private fun renderState(result: Resource<List<Vacancy>>) {
        when (result) {
            is Resource.NoInternetError -> _uiState.value = handleNoInternetError()
            is Resource.ServerError -> _uiState.value = handleServerError()
            is Resource.Success -> if (result.data.isEmpty()) {
                _uiState.value = UiScreenState.Empty
            } else {
                val vacanciesUi = result.data.map { vacancy ->
                    mappingVacancyToVacancyUi(vacancy)
                }
                currentPage = result.page ?: currentPage
                maxPages = result.pages ?: maxPages
                _vacanciesList.value = _vacanciesList.value?.plus(vacanciesUi)
                isFirstSearch = false
                totalVacanciesFound = result.found ?: totalVacanciesFound
                _uiState.value = UiScreenState.Success(
                    vacancies = _vacanciesList.value ?: emptyList(),
                    found = totalVacanciesFound
                )
            }
        }
    }

    private fun handleNoInternetError(): UiScreenState {
        return if (isFirstSearch) {
            UiScreenState.NoInternetError
        } else {
            _errorEvent.value = "no_internet"
            _isNextPageLoading.value = false
            UiScreenState.Success(
                vacancies = _vacanciesList.value ?: emptyList(),
                found = totalVacanciesFound
            )

        }
    }

    private fun handleServerError(): UiScreenState {
        return if (isFirstSearch) {
            UiScreenState.ServerError
        } else {
            _isNextPageLoading.value = false
            _errorEvent.value = "server_error"
            UiScreenState.Success(
                vacancies = _vacanciesList.value ?: emptyList(),
                found = totalVacanciesFound
            )
        }
    }

    private fun mappingVacancyToVacancyUi(vacancy: Vacancy): VacancyUi {
        return VacancyUi(
            id = vacancy.id,
            name = getName(vacancy.name, vacancy.areaName),
            salary = vacancy.salary.formatSalary(),
            employerName = vacancy.employer.name ?: "",
            logoUrl = vacancy.employer.logoUrl
        )
    }

    private fun getName(name: String, areaName: String): String {
        return if (areaName.isEmpty()) {
            name
        } else {
            "$name, $areaName"
        }
    }

    private fun isFiltersChanged(): Boolean {
        val savedFilters = filterInteractor.loadFilterSettings()

        return filterLocation != savedFilters.location ||
            filterIndustryId != savedFilters.industryId ||
            filterSalary != savedFilters.salary ||
            hideWithoutSalary != savedFilters.hideWithoutSalary
    }

}
