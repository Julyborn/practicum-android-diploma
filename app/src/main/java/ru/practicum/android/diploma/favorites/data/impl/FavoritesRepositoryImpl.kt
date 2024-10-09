package ru.practicum.android.diploma.favorites.data.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.favorites.data.db.AppDatabase
import ru.practicum.android.diploma.favorites.data.db.converters.VacancyDetailsDbConverter
import ru.practicum.android.diploma.favorites.data.db.entity.VacancyDetailsEntity
import ru.practicum.android.diploma.favorites.domain.api.FavoritesRepository
import ru.practicum.android.diploma.vacancies.domain.models.VacancyDetails

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val vacancyDetailsDbConverter: VacancyDetailsDbConverter,
) : FavoritesRepository {
    override fun getAll(): Flow<List<VacancyDetails>> {
        return appDatabase.getVacancyDetails().getAll()
            .map { vacancyDetailsEntity -> convertFromVacancyDetailsEntity(vacancyDetailsEntity) }
    }

    override suspend fun addVacancy(vacancyDetails: VacancyDetails) {
        val vacancyDetailsEntity = vacancyDetailsDbConverter.map(vacancyDetails)
        appDatabase.getVacancyDetails().addVacancy(vacancyDetailsEntity)
    }

    override suspend fun removeVacancy(id: String) {
        appDatabase.getVacancyDetails().deleteVacancyById(id)
    }

    override suspend fun getVacancyById(id: String): VacancyDetails? {
        val vacancyDetailsEntity = appDatabase.getVacancyDetails().getVacancyById(id)
        return vacancyDetailsEntity?.let { vacancyDetailsDbConverter.map(it) }
    }

    private fun convertFromVacancyDetailsEntity(collection: List<VacancyDetailsEntity>): List<VacancyDetails> {
        return collection.map { vacancyDetailsEntity -> vacancyDetailsDbConverter.map(vacancyDetailsEntity) }
    }

}
