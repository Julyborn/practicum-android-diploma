package ru.practicum.android.diploma.filter.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.filter.data.dto.FilterSettings
import ru.practicum.android.diploma.filter.domain.api.FilterInteractor
import ru.practicum.android.diploma.filter.domain.api.WorkplaceInteractor
import ru.practicum.android.diploma.filter.domain.models.Country
import ru.practicum.android.diploma.filter.domain.models.Region

class FilterViewModel(
    private val filterInteractor: FilterInteractor,
    private val workplaceInteractor: WorkplaceInteractor
) : ViewModel() {

    private var initialFilterSettings: FilterSettings? = null

    private var initialCountry: Country? = null
    private var initialRegion: Region? = null

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

    private val _isApplyButtonVisible = MutableLiveData<Boolean>()
    val isApplyButtonVisible: LiveData<Boolean> get() = _isApplyButtonVisible

    private val _isResetButtonVisible = MutableLiveData<Boolean>()
    val isResetButtonVisible: LiveData<Boolean> get() = _isResetButtonVisible

    init {
        loadFilters()
    }

    fun loadFilters() {
        val settings = filterInteractor.loadFilterSettings()

        initialFilterSettings = FilterSettings(
            location = settings.location.orEmpty(),
            salary = settings.salary.orEmpty(),
            industryId = settings.industryId.orEmpty(),
            industry = settings.industry.orEmpty(),
            hideWithoutSalary = settings.hideWithoutSalary,
            area = settings.area.orEmpty()
        )
        initialCountry = workplaceInteractor.getSelectedCountry()
        initialRegion = workplaceInteractor.getSelectedRegion()

        _hideWithoutSalary.value = settings.hideWithoutSalary
        _salary.value = settings.salary ?: ""
        _location.value = settings.location ?: ""
        _industry.value = settings.industry
        _industryId.value = settings.industryId
        _area.value = settings.area

        checkResetButtonVisibility()
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
        initialFilterSettings = filterSettings
    }

    fun hasActiveFilters(): Boolean {
        return _location.value.orEmpty().isNotEmpty() ||
            _salary.value.orEmpty().isNotEmpty() ||
            _industry.value.orEmpty().isNotEmpty() ||
            _location.value.orEmpty().isNotEmpty() ||
            _area.value.orEmpty().isNotEmpty() ||
            _hideWithoutSalary.value == true
    }

    private fun hasFiltersChanged(): Boolean {
        return initialFilterSettings?.let { initial ->
            _hideWithoutSalary.value != initial.hideWithoutSalary ||
                _salary.value.orEmpty() != initial.salary.orEmpty() ||
                _location.value.orEmpty() != initial.location.orEmpty() ||
                _industryId.value.orEmpty() != initial.industryId.orEmpty() ||
                _industry.value.orEmpty() != initial.industry.orEmpty() ||
                _area.value.orEmpty() != initial.area.orEmpty()
        } ?: false
    }

    private fun checkResetButtonVisibility() {
        _isResetButtonVisible.value = hasActiveFilters()
    }

    private fun checkApplyButtonVisibility() {
        _isApplyButtonVisible.value = hasFiltersChanged()
    }

    fun clearFilters() {
        updateHideWithoutSalary(false)
        clearSalary()
        clearJobFilter()
        clearIndustry()
    }

    fun updateHideWithoutSalary(checked: Boolean) {
        _hideWithoutSalary.value = checked
        checkApplyButtonVisibility()
        checkResetButtonVisibility()
    }

    fun updateSalary(newSalary: String) {
        _salary.value = newSalary
        checkApplyButtonVisibility()
        checkResetButtonVisibility()
    }

    fun clearSalary() {
        _salary.value = ""
        checkApplyButtonVisibility()
        checkResetButtonVisibility()
    }

    fun setLocation(newLocation: String) {
        _location.value = newLocation
        checkApplyButtonVisibility()
        checkResetButtonVisibility()
    }

    fun clearLocation() {
        _location.value = ""
        checkApplyButtonVisibility()
        checkResetButtonVisibility()
    }

    fun updateIndustry(industryId: String?, industry: String?) {
        _industryId.value = industryId
        _industry.value = industry
        checkApplyButtonVisibility()
        checkResetButtonVisibility()
    }

    fun clearIndustry() {
        updateIndustry(null, null)
        checkApplyButtonVisibility()
        checkResetButtonVisibility()
    }

    fun setArea(areaId: String?) {
        _area.value = areaId
        checkApplyButtonVisibility()
        checkResetButtonVisibility()
    }
    fun restoreWorkplace() {
        workplaceInteractor.saveSelectedCountry(initialCountry)
        workplaceInteractor.saveSelectedRegion(initialRegion)
    }
    fun clearJobFilter() {
        clearLocation()
        setArea(null)
        checkApplyButtonVisibility()
        checkResetButtonVisibility()
        workplaceInteractor.clearSavedCountry()
        workplaceInteractor.clearSavedRegion()
    }
}
