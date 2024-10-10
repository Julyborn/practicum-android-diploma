package ru.practicum.android.diploma.filter.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WorkplaceViewModel : ViewModel() {
    private val _selectedCountry = MutableLiveData<String?>()
    val selectedCountry: LiveData<String?> get() = _selectedCountry

    private val _isCountrySelected = MutableLiveData(false)
    val isCountrySelected: LiveData<Boolean> get() = _isCountrySelected

    fun selectCountry(country: String) {
        _selectedCountry.value = country
        _isCountrySelected.value = true
    }

    fun clearCountry() {
        _selectedCountry.value = null
        _isCountrySelected.value = false
    }
}
