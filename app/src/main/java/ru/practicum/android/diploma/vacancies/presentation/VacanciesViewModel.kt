package ru.practicum.android.diploma.vacancies.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.practicum.android.diploma.vacancies.domain.api.VacanciesInteractor
import java.io.IOException

class VacanciesViewModel(private val vacanciesInteractor: VacanciesInteractor) : ViewModel() {
    private val _screenState = MutableLiveData<VacancyScreenState>()
    val screenState: LiveData<VacancyScreenState> = _screenState

    fun loadVacancyDetails(vacancyId: String) {
        viewModelScope.launch {
            _screenState.value = VacancyScreenState.Loading
            try {
                val details = vacanciesInteractor.getVacancyDetails(vacancyId)
                _screenState.value = VacancyScreenState.Success(details)
            } catch (e: IOException) {
                _screenState.value = VacancyScreenState.Error(VacancyScreenState.ErrorType.SERVER_ERROR)
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    _screenState.value = VacancyScreenState.Error(VacancyScreenState.ErrorType.NOT_FOUND)
                } else {
                    _screenState.value = VacancyScreenState.Error(VacancyScreenState.ErrorType.SERVER_ERROR)
                }
            }
        }
    }
}
