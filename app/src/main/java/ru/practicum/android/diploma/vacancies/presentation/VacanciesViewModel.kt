package ru.practicum.android.diploma.vacancies.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.practicum.android.diploma.favorites.domain.api.FavoritesInteractor
import ru.practicum.android.diploma.search.data.network.NetworkUtils
import ru.practicum.android.diploma.vacancies.domain.api.VacanciesInteractor
import ru.practicum.android.diploma.vacancies.domain.models.VacancyDetails
import java.io.IOException

class VacanciesViewModel(
    private val vacanciesInteractor: VacanciesInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {
    private val _screenState = MutableLiveData<VacancyScreenState>()
    val screenState: LiveData<VacancyScreenState> = _screenState

    private var currentVacancyDetails: VacancyDetails? = null

    private val _isFavorite = MutableLiveData<Boolean?>(null)
    val isFavorite: LiveData<Boolean?> = _isFavorite

    fun loadVacancyDetails(vacancyId: String) {
        viewModelScope.launch {
            _screenState.value = VacancyScreenState.Loading
            try {
                val details = vacanciesInteractor.getVacancyDetails(vacancyId)
                currentVacancyDetails = details

                val favoriteVacancy = favoritesInteractor.getVacancyById(vacancyId)
                _isFavorite.postValue(favoriteVacancy != null)

                _screenState.value = VacancyScreenState.Success(details)
            } catch (e: NetworkUtils.NoInternetException) {
                _screenState.value = VacancyScreenState.Error(VacancyScreenState.ErrorType.NO_INTERNET)
                Log.e("VacanciesViewModel", "catch NoInternetException: ${e.message}", e)
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

    fun addToFavoriteHandler() {
        val isCurrentlyFavorite = isFavorite.value == true
        _isFavorite.postValue(!isCurrentlyFavorite)
        viewModelScope.launch {
            if (isCurrentlyFavorite) {
                currentVacancyDetails?.let { favoritesInteractor.removeVacancy(it.id) }
            } else {
                currentVacancyDetails?.let { favoritesInteractor.addVacancy(it) }
            }
        }
    }

}
