package ru.practicum.android.diploma.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.favorites.data.db.AppDatabase
import ru.practicum.android.diploma.favorites.data.db.converters.VacancyDetailsDbConverter
import ru.practicum.android.diploma.filter.data.impl.FilterPreferencesImpl
import ru.practicum.android.diploma.filter.data.impl.FilterRepositoryImpl
import ru.practicum.android.diploma.filter.data.impl.IndustryRepositoryImp
import ru.practicum.android.diploma.filter.domain.api.FilterInteractor
import ru.practicum.android.diploma.filter.domain.api.FilterPreferences
import ru.practicum.android.diploma.filter.domain.api.FilterRepository
import ru.practicum.android.diploma.filter.domain.api.IndustryInteractor
import ru.practicum.android.diploma.filter.domain.api.IndustryInteractorImpl
import ru.practicum.android.diploma.filter.domain.api.IndustryRepository
import ru.practicum.android.diploma.filter.domain.impl.FilterInteractorImpl
import ru.practicum.android.diploma.filter.domain.models.IndustryViewModel
import ru.practicum.android.diploma.filter.presentation.FilterViewModel
import ru.practicum.android.diploma.search.data.network.HeadHunterAPI
import ru.practicum.android.diploma.search.data.network.RetrofitInstance

private const val APPLICATION_PREFERENCES = "application_preferences"

val dataModule = module {

    factory { Gson() }

    single {
        androidContext().getSharedPreferences(APPLICATION_PREFERENCES, Context.MODE_PRIVATE)
    }

    single<HeadHunterAPI> {
        RetrofitInstance.headHunterAPI
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db").build()
    }

    single {
        VacancyDetailsDbConverter(get())
    }

    single<IndustryRepository> {
        IndustryRepositoryImp(get(), get())
    }

    single<IndustryInteractor> {
        IndustryInteractorImpl(get())
    }

    single<FilterInteractor> {
        FilterInteractorImpl(get())
    }

    single<FilterRepository> {
        FilterRepositoryImpl(get(), get())
    }

    single<FilterPreferences> {
        FilterPreferencesImpl(get())
    }

    viewModel<FilterViewModel> {
        FilterViewModel(get())
    }
    viewModel<IndustryViewModel> {
        IndustryViewModel(get())
    }
}
