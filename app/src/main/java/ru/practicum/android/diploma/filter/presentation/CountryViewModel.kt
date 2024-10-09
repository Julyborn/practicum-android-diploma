package ru.practicum.android.diploma.filter.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.filter.domain.api.CountryInteractor
import java.io.IOException

class CountryViewModel(private val countryInteractor: CountryInteractor) : ViewModel() {

    private val _countries = MutableLiveData<CountryState>()
    val countries: LiveData<CountryState> = _countries

    fun loadCountries() {
        viewModelScope.launch {
            try {
                val countriesList = countryInteractor.getCountries()
                _countries.value = CountryState.Success(countriesList)
            } catch (e: IOException) {
                _countries.value = CountryState.Error
            }
        }
    }
}
