package ru.practicum.android.diploma.di

import android.content.Context
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.filter.data.impl.IndustryRepositoryImp
import ru.practicum.android.diploma.filter.domain.api.IndustryInteractor
import ru.practicum.android.diploma.filter.domain.api.IndustryInteractorImpl
import ru.practicum.android.diploma.filter.domain.api.IndustryRepository
import ru.practicum.android.diploma.filter.domain.models.IndustryViewModel
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

    single<IndustryRepository> {
        IndustryRepositoryImp(get(), get())
    }

    single<IndustryInteractor> {
        IndustryInteractorImpl(get())
    }

    viewModel<IndustryViewModel> {
        IndustryViewModel(get())
    }
}
