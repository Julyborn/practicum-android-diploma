package ru.practicum.android.diploma.filter.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.filter.data.dto.FilterSettings
import ru.practicum.android.diploma.filter.domain.api.FilterInteractor


class FilterViewModel(private val filterInteractor: FilterInteractor) : ViewModel() {

    private val _hideWithoutSalary = MutableLiveData<Boolean>()
    val hideWithoutSalary: LiveData<Boolean> get() = _hideWithoutSalary

    private val _salary = MutableLiveData<String>("")
    val salary: LiveData<String> get() = _salary

    private val _location = MutableLiveData<String>("")
    val location: LiveData<String> get() = _location

    private val _industry = MutableLiveData<String?>()
    val industry: LiveData<String?> get() = _industry

    private val _area = MutableLiveData<String?>()
    val area: LiveData<String?> get() = _area

    fun loadFilters(): FilterSettings {
        val settings = filterInteractor.loadFilterSettings()
        _hideWithoutSalary.value = settings.hideWithoutSalary
        _salary.value = settings.salary ?: ""
        _location.value = settings.location ?: ""
        _industry.value = settings.industry
        return settings
    }

    fun applyFilters() {
        val filterSettings = FilterSettings(
            location = _location.value,
            salary = _salary.value,
            industry = _industry.value,
            hideWithoutSalary = _hideWithoutSalary.value ?: false,
        )
        filterInteractor.saveFilterSettings(filterSettings)
    }

    fun clearFilters() {
        filterInteractor.clearFilterSettings()
        updateHideWithoutSalary(false)
        clearSalary()
        clearLocation()
        clearIndustry()
    }

    fun updateHideWithoutSalary(checked: Boolean) {
        _hideWithoutSalary.value = checked
    }

    fun updateSalary(newSalary: String) {
        _salary.value = newSalary
    }

    fun clearSalary() {
        _salary.value = ""
    }

    fun updateLocation(newLocation: String) {
        _location.value = newLocation
    }

    fun clearLocation() {
        _location.value = ""
    }

    fun updateIndustry(industry: String?) {
        _industry.value = industry
    }

    fun clearIndustry() {
        updateIndustry(null)
    }

    fun clearJobFilter() {
        clearLocation()
    }
}
