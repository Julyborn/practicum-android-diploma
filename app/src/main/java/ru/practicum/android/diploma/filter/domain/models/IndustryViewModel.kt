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
    val uiState: LiveData<UiScreenState>
        get() = _uiState

    private val _industriesList = MutableLiveData<List<IndustryDto>>(emptyList())
    val industriesList: LiveData<List<IndustryDto>> get() = _industriesList

    private val _searchQuery = MutableLiveData<String>("")

    private var searchJob: Job? = null

    private var filterIndustry: String? = null

    private var currentPage = 0
    private var maxPages = Int.MAX_VALUE
    private var isNextPageLoading = false
    private var isFirstSearch = false

    fun onSearchQueryChanged(query: String) {
        currentPage = 0
        maxPages = Int.MAX_VALUE
        _industriesList.value = emptyList()
        _searchQuery.value = query

        if (query.isEmpty()) {
            searchJob?.cancel()
            _uiState.value = UiScreenState.Default
            return
        }
        val params = buildSearchParams(query)
        _uiState.value = UiScreenState.Loading
        searchRequest(params)
    }

    fun onLastItemReached() {
        if (isNextPageLoading || currentPage >= maxPages - 1) return
        isNextPageLoading = true
        val query = _searchQuery.value ?: return
        val params = buildSearchParams(query = query, page = currentPage + 1)
        searchRequest(params)
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
            is Resource.Success -> if (result.data.isEmpty()) {
                _uiState.value = UiScreenState.Empty
            } else {
                val filteredList = filterResults(result.data, _searchQuery.value ?: "")
                currentPage = result.page ?: currentPage
                maxPages = result.pages ?: maxPages
                _industriesList.value = _industriesList.value?.plus(filteredList)
                isFirstSearch = false
                _uiState.value = UiScreenState.Default
            }
        }
    }

    private fun handleNoInternetError(): UiScreenState {
        return if (isFirstSearch) {
            UiScreenState.NoInternetError
        } else {
            isNextPageLoading = false
            UiScreenState.Default
        }
    }

    private fun handleServerError(): UiScreenState {
        return if (isFirstSearch) {
            UiScreenState.ServerError
        } else {
            isNextPageLoading = false
            UiScreenState.Default
        }
    }

    fun loadAllIndustries() {
        currentPage = 0
        maxPages = Int.MAX_VALUE
        _uiState.value = UiScreenState.Loading

        searchRequest(buildSearchParams(query = ""))
    }
}
