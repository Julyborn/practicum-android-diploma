package ru.practicum.android.diploma.vacancies.presentation

import android.util.Log
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
                Log.e("VacanciesViewModel", "catch IOException: ${e.localizedMessage}", e)
                _screenState.value = VacancyScreenState.Error(VacancyScreenState.ErrorType.SERVER_ERROR)
            } catch (e: HttpException) {
                if (e.code() == HTTP_NOT_FOUND) {
                    _screenState.value = VacancyScreenState.Error(VacancyScreenState.ErrorType.NOT_FOUND)
                } else {
                    _screenState.value = VacancyScreenState.Error(VacancyScreenState.ErrorType.SERVER_ERROR)
                }
            }
        }
    }
    companion object {
        private const val HTTP_NOT_FOUND = 404
    }
}
