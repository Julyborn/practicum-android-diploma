package ru.practicum.android.diploma.filter.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filter.domain.api.WorkplaceInteractor
import ru.practicum.android.diploma.filter.domain.models.Country
import ru.practicum.android.diploma.filter.domain.models.Region
import java.io.IOException

class RegionChoosingViewModel(private val interactor: WorkplaceInteractor) : ViewModel() {

    private val _state = MutableLiveData<WorkplaceState>()
    val state: LiveData<WorkplaceState> = _state

    private var countryCache: List<Country>? = null
    private var allRegions: List<Region> = emptyList()

    init {
        loadRegions()
        viewModelScope.launch {
            countryCache = interactor.getCountries()
        }
    }

    fun loadRegions() {
        _state.value = WorkplaceState.Loading
        val country = interactor.getSelectedCountry()
        if (country != null) {
            loadRegionsByCountry(country.id)
        } else {
            loadAllRegions()
        }
    }

    private fun loadAllRegions() {
        viewModelScope.launch {
            try {
                val regions = interactor.getAllRegions()
                allRegions = regions
                if (regions.isEmpty()) {
                    _state.value = WorkplaceState.FetchError
                } else {
                    _state.value = WorkplaceState.Success(emptyList(), regions)
                }
            } catch (e: IOException) {
                _state.value = WorkplaceState.FetchError
                throw e
            }
        }
    }

    private fun loadRegionsByCountry(countryId: String) {
        viewModelScope.launch {
            try {
                val regions = interactor.getRegionsByCountry(countryId)
                allRegions = regions
                if (regions.isEmpty()) {
                    _state.value = WorkplaceState.FetchError
                } else {
                    _state.value = WorkplaceState.Success(emptyList(), regions)
                }
            } catch (e: IOException) {
                _state.value = WorkplaceState.FetchError
                throw e
            }
        }
    }

    fun filterRegions(query: String) {
        val filteredRegions = if (query.isEmpty()) {
            allRegions
        } else {
            allRegions.filter { it.name.contains(query, ignoreCase = true) }
        }
        _state.value = if (filteredRegions.isEmpty()) {
            WorkplaceState.NoRegionsError
        } else {
            WorkplaceState.Success(emptyList(), filteredRegions)
        }
    }

    fun selectRegion(region: Region) {
        interactor.saveSelectedRegion(region)
        getCountryById(region.parentId)
    }

    private fun getCountryById(countryId: String?) {
        viewModelScope.launch {
            if (countryCache == null) {
                countryCache = interactor.getCountries()
            }
            val country = countryCache?.find { it.id == countryId }
            if (country != null) {
                interactor.saveSelectedCountry(country)
            }
        }
    }
}
