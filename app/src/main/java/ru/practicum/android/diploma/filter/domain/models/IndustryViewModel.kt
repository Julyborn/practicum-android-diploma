package ru.practicum.android.diploma.filter.domain.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filter.data.dto.IndustryDto
import ru.practicum.android.diploma.filter.domain.api.IndustryInteractor
import ru.practicum.android.diploma.search.domain.models.Resource
import ru.practicum.android.diploma.search.presentation.models.UiScreenState

class IndustryViewModel(private val industryInteractor: IndustryInteractor) : ViewModel() {

    private val _uiState = MutableLiveData<UiScreenState>(UiScreenState.Default)
    val uiState: LiveData<UiScreenState> get() = _uiState

    private val _isLoadingAllIndustries = MutableLiveData<Boolean>(false)
    val isLoadingAllIndustries: LiveData<Boolean> get() = _isLoadingAllIndustries

    private val _industriesList = MutableLiveData<List<IndustryDto>>(emptyList())
    val industriesList: LiveData<List<IndustryDto>> get() = _industriesList

    private val _searchQuery = MutableLiveData<String>("")

    private var searchJob: Job? = null

    private var filterIndustry: String? = null
    private var currentPage = 0
    private var maxPages = Int.MAX_VALUE
    private var isNextPageLoading = false
    private var isFirstSearch = true

    private var allIndustriesList: List<IndustryDto> = emptyList()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) {
            _industriesList.value = allIndustriesList
            return
        }
        _uiState.value = UiScreenState.Loading
        val filteredIndustries = filterResults(allIndustriesList, query)
        _uiState.value = UiScreenState.Default
        _industriesList.value = filteredIndustries
    }

    private fun buildSearchParams(query: String, page: Int = 0): IndustrySearchParams {
        return IndustrySearchParams(
            query = query,
            industry = filterIndustry,
            page = page
        )
    }

    private fun filterResults(industries: List<IndustryDto>, query: String): List<IndustryDto> {
        return industries.filter { it.name.contains(query, ignoreCase = true) }
    }

    private fun searchRequest(params: IndustrySearchParams) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            industryInteractor.searchIndustries(params).collect { result ->
                renderState(result)
                isNextPageLoading = false
            }
        }
    }

    private fun renderState(result: Resource<List<IndustryDto>>) {
        when (result) {
            is Resource.NoInternetError -> _uiState.value = UiScreenState.NoInternetError
            is Resource.ServerError -> _uiState.value = UiScreenState.ServerError
            is Resource.Success -> {
                if (result.data.isEmpty()) {
                    _uiState.value = UiScreenState.Empty
                } else {
                    allIndustriesList = result.data.toList()
                    currentPage = result.page ?: currentPage
                    maxPages = result.pages ?: maxPages
                    _industriesList.value = allIndustriesList
                    _uiState.value = UiScreenState.Default

                    isFirstSearch = false
                    _isLoadingAllIndustries.value = false
                }
            }
        }
    }

    fun loadAllIndustries() {
        currentPage = 0
        maxPages = Int.MAX_VALUE
        _uiState.value = UiScreenState.Loading
        _isLoadingAllIndustries.value = true
        searchRequest(buildSearchParams(query = ""))
    }

    fun onIndustriesLoaded() {
        _isLoadingAllIndustries.value = false
    }
}
