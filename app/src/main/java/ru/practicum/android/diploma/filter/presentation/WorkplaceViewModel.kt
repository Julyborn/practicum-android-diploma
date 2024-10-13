package ru.practicum.android.diploma.filter.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.filter.domain.api.WorkplaceInteractor
import ru.practicum.android.diploma.filter.domain.models.Country
import ru.practicum.android.diploma.filter.domain.models.Region

class WorkplaceViewModel(private val interactor: WorkplaceInteractor) : ViewModel() {

    private val _selectedCountry = MutableLiveData<Country?>()
    val selectedCountry: LiveData<Country?> get() = _selectedCountry
    private val _selectedRegion = MutableLiveData<Region?>()
    val selectedRegion: LiveData<Region?> get() = _selectedRegion

    init {
        loadSavedWorkplaceSettings()
    }

    fun loadSavedWorkplaceSettings() {
        _selectedCountry.value = interactor.getSelectedCountry()
        _selectedRegion.value = interactor.getSelectedRegion()
    }

    fun clearCountry() {
        _selectedCountry.value = null
        interactor.clearSavedCountry()
        clearRegion()
    }

    fun getSelectedCountry(): Country? {
        return interactor.getSelectedCountry()
    }

    fun getSelectedRegion(): Region? {
        return interactor.getSelectedRegion()
    }

    fun clearRegion() {
        _selectedRegion.value = null
        interactor.clearSavedRegion()
    }

}
