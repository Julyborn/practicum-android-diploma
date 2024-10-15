package ru.practicum.android.diploma.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.practicum.android.diploma.favorites.data.db.AppDatabase
import ru.practicum.android.diploma.favorites.data.db.converters.VacancyDetailsDbConverter
import ru.practicum.android.diploma.search.data.network.HeadHunterAPI
import ru.practicum.android.diploma.search.data.network.RetrofitInstance
import ru.practicum.android.diploma.search.domain.api.SearchInteractor
import ru.practicum.android.diploma.search.domain.impl.SearchInteractorImpl
import ru.practicum.android.diploma.search.presentation.SearchViewModel

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
}
