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

    private val _industryId = MutableLiveData<String?>()
    val industryId: LiveData<String?> get() = _industryId

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
        _industryId.value = settings.industryId
        _area.value = settings.area
        return settings
    }

    fun applyFilters() {
        val filterSettings = FilterSettings(
            location = _location.value,
            salary = _salary.value,
            industryId = _industryId.value,
            industry = _industry.value,
            hideWithoutSalary = _hideWithoutSalary.value ?: false,
            area = _area.value
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

    fun setLocation(newLocation: String) {
        _location.value = newLocation
    }

    fun clearLocation() {
        _location.value = ""
    }

    fun updateIndustry(industryId: String?, industry: String?) {
        _industryId.value = industryId
        _industry.value = industry
    }

    fun clearIndustry() {
        updateIndustry(null, null)
    }

    fun setArea(areaId: String?) {
        _area.value = areaId
    }

    fun clearJobFilter() {
        clearLocation()
        setArea(null)
    }
}
