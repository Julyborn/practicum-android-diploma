package ru.practicum.android.diploma.filter.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filter.domain.api.WorkplaceInteractor
import ru.practicum.android.diploma.filter.domain.models.Country

class CountryChoosingViewModel(private val interactor: WorkplaceInteractor) : ViewModel() {

    private val _state = MutableLiveData<WorkplaceState>()
    val state: LiveData<WorkplaceState> = _state

    init {
        loadCountries()
    }

    fun loadCountries() {
        _state.value = WorkplaceState.Loading
        viewModelScope.launch {
            val contries = interactor.getCountries()
            if (contries.isEmpty()) {
                _state.value = WorkplaceState.FetchError
            } else {
                _state.value = WorkplaceState.Success(contries, emptyList())
            }
        }
    }

    fun selectCountry(country: Country) {
        interactor.saveSelectedCountry(country)
        interactor.clearSavedRegion()
    }
}
