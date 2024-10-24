package ru.practicum.android.diploma.filter.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filter.domain.api.WorkplaceInteractor
import ru.practicum.android.diploma.filter.domain.models.Country
import ru.practicum.android.diploma.filter.domain.models.Region
import ru.practicum.android.diploma.search.domain.models.Resource

class RegionChoosingViewModel(private val interactor: WorkplaceInteractor) : ViewModel() {

    private val _uiState = MutableLiveData<WorkplaceState>(WorkplaceState.Loading)
    val uiState: LiveData<WorkplaceState> get() = _uiState

    private val _regionsList = MutableLiveData<List<Region>>(emptyList())
    val regionsList: LiveData<List<Region>> get() = _regionsList

    private var allRegions: List<Region> = emptyList()
    private var searchJob: Job? = null
    var isNoInternet: Boolean = false

    private var countryCache: List<Country>? = null

    init {
        loadCountries()
    }

    fun retryLoadingData() {
        loadCountries()
    }

    private fun loadCountries() {
        _uiState.value = WorkplaceState.Loading
        viewModelScope.launch {
            val result = interactor.getCountries()
            handleCountriesResult(result)
        }
    }

    private suspend fun handleCountriesResult(result: Resource<List<Country>>) {
        when (result) {
            is Resource.Success -> {
                isNoInternet = false
                countryCache = result.data
                allRegions = countryCache?.flatMap { country ->
                    val regionsResult = interactor.getRegionsByCountry(country.id)
                    if (regionsResult is Resource.Success) {
                        regionsResult.data
                    } else {
                        emptyList()
                    }
                } ?: emptyList()

                _uiState.value = if (allRegions.isEmpty()) {
                    WorkplaceState.NoRegionsError
                } else {
                    WorkplaceState.Success(countryCache ?: emptyList(), allRegions)
                }
            }

            is Resource.NoInternetError -> {
                isNoInternet = true
                _uiState.value = WorkplaceState.NoInternet
            }

            is Resource.ServerError -> {
                _uiState.value = WorkplaceState.FetchError
            }
        }
    }

    fun filterRegions(query: String) {
        if (query.isEmpty()) {
            _regionsList.value = allRegions
            _uiState.value = if (allRegions.isEmpty()) {
                WorkplaceState.NoRegionsError
            } else {
                WorkplaceState.Success(countryCache ?: emptyList(), allRegions)
            }
        }

        if (isNoInternet) {
            _uiState.value = WorkplaceState.NoInternet

            if (!isNoInternet) {
                isNoInternet = false
                retryLoadingData()
            }
            return
        }
        _uiState.value = WorkplaceState.Loading
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val filteredRegions = allRegions.filter { it.name.contains(query, ignoreCase = true) }
            _regionsList.value = filteredRegions

            _uiState.value = if (filteredRegions.isEmpty()) {
                WorkplaceState.NoRegionsError
            } else {
                WorkplaceState.Success(countryCache ?: emptyList(), filteredRegions)
            }
        }
    }

    fun selectRegion(region: Region) {
        interactor.saveSelectedRegion(region)
        getCountryById(region.parentId)
    }

    private fun getCountryById(countryId: String?) {
        viewModelScope.launch {
            if (countryCache == null) {
                val result = interactor.getCountries()
                handleCountriesResult(result)
            }

            countryCache?.find { it.id == countryId }?.let {
                interactor.saveSelectedCountry(it)
            }
        }
    }
}
