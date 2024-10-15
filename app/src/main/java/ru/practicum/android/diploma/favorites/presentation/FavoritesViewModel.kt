package ru.practicum.android.diploma.favorites.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.favorites.domain.api.FavoritesInteractor
import ru.practicum.android.diploma.search.presentation.models.UiScreenState
import ru.practicum.android.diploma.search.presentation.models.VacancyUi
import ru.practicum.android.diploma.util.formatSalary
import ru.practicum.android.diploma.vacancies.domain.models.VacancyDetails
import java.io.IOException

class FavoritesViewModel(private val favoritesInteractor: FavoritesInteractor) : ViewModel() {

    private val _uiState = MutableLiveData<UiScreenState>(UiScreenState.Empty)
    val uiState: LiveData<UiScreenState>
        get() = _uiState

    init {
        viewModelScope.launch {
            try {
                favoritesInteractor.getAll().collect { vacancies ->
                    renderState(vacancies)
                }
            } catch (e: IOException) {
                Log.e("FavoritesViewModel", "catch IOException: ${e.localizedMessage}", e)
                _uiState.value = UiScreenState.ServerError
            }
        }
    }

    private fun renderState(vacancies: List<VacancyDetails>) {
        if (vacancies.isEmpty()) {
            _uiState.value = UiScreenState.Empty
        } else {
            val vacanciesUi = vacancies.map { vacancy ->
                mappingVacancyDetailsToVacancyUi(vacancy)
            }
            _uiState.value = UiScreenState.Success(
                vacancies = vacanciesUi,
                found = vacanciesUi.count()
            )
        }
    }

    private fun mappingVacancyDetailsToVacancyUi(vacancy: VacancyDetails): VacancyUi {
        return VacancyUi(
            id = vacancy.id,
            name = getName(vacancy.name, vacancy.areaName ?: ""),
            salary = vacancy.salary.formatSalary(),
            employerName = vacancy.employer?.name ?: "",
            logoUrl = vacancy.employer?.logoUrl
        )
    }

    private fun getName(name: String, areaName: String): String {
        return if (areaName.isEmpty()) {
            name
        } else {
            "$name, $areaName"
        }
    }

}
