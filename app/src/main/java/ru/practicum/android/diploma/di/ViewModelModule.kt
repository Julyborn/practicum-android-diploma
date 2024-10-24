package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.favorites.presentation.FavoritesViewModel
import ru.practicum.android.diploma.filter.presentation.CountryChoosingViewModel
import ru.practicum.android.diploma.filter.presentation.FilterViewModel
import ru.practicum.android.diploma.filter.presentation.RegionChoosingViewModel
import ru.practicum.android.diploma.filter.presentation.WorkplaceViewModel
import ru.practicum.android.diploma.search.presentation.SearchViewModel
import ru.practicum.android.diploma.vacancies.presentation.VacanciesViewModel

val viewModelModule = module {

    viewModel<FavoritesViewModel> {
        FavoritesViewModel(get())
    }

    viewModel<FilterViewModel> {
        FilterViewModel(get(), get())
    }

    viewModel<SearchViewModel> {
        SearchViewModel(get(), get())
    }

    viewModel<VacanciesViewModel> {
        VacanciesViewModel(get(), get())
    }

    viewModel<WorkplaceViewModel> {
        WorkplaceViewModel(get())
    }

    viewModel<CountryChoosingViewModel> {
        CountryChoosingViewModel(get())
    }

    viewModel<RegionChoosingViewModel> {
        RegionChoosingViewModel(get())
    }
}
