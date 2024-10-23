package ru.practicum.android.diploma.filter.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.filter.data.dto.FilterSettings
import ru.practicum.android.diploma.filter.domain.api.WorkplaceInteractor
import ru.practicum.android.diploma.filter.domain.models.Country
import ru.practicum.android.diploma.filter.domain.models.Region

class WorkplaceViewModel(private val interactor: WorkplaceInteractor) : ViewModel() {

    private val _selectedCountry = MutableLiveData<Country?>()
    val selectedCountry: LiveData<Country?> get() = _selectedCountry
    private val _selectedRegion = MutableLiveData<Region?>()
    val selectedRegion: LiveData<Region?> get() = _selectedRegion
    private var initialCountry:  Country? = null
    private var initialRegion: Region? = null

    fun loadSavedWorkplaceSettings() {
        initialCountry = interactor.getSelectedCountry()
        initialRegion = interactor.getSelectedRegion()
        _selectedCountry.value = initialCountry
        _selectedRegion.value = initialRegion
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
    fun restoreWorkplace() {
        interactor.saveSelectedCountry(initialCountry)
        interactor.saveSelectedRegion(initialRegion)
    }
    fun clearRegion() {
        _selectedRegion.value = null
        interactor.clearSavedRegion()
    }

}
