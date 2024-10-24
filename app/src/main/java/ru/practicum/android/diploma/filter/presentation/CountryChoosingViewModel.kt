package ru.practicum.android.diploma.filter.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filter.domain.api.WorkplaceInteractor
import ru.practicum.android.diploma.filter.domain.models.Country
import ru.practicum.android.diploma.search.domain.models.Resource

class CountryChoosingViewModel(private val interactor: WorkplaceInteractor) : ViewModel() {

    private val _state = MutableLiveData<WorkplaceState>()
    val state: LiveData<WorkplaceState> = _state

    init {
        loadCountries()
    }

    fun loadCountries() {
        _state.value = WorkplaceState.Loading
        viewModelScope.launch {
            when (val result = interactor.getCountries()) {
                is Resource.Success -> _state.value = WorkplaceState.Success(result.data, emptyList())
                is Resource.NoInternetError -> _state.value = WorkplaceState.NoInternet
                is Resource.ServerError -> _state.value = WorkplaceState.FetchError
            }
        }
    }

    fun selectCountry(country: Country) {
        interactor.saveSelectedCountry(country)
        interactor.clearSavedRegion()
    }
}
