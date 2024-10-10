package ru.practicum.android.diploma.filter.ui

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.filter.domain.models.Country

class CountryViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
    private val countryName: TextView = rootView.findViewById(R.id.countryName)

    fun bind(country: Country, onCountrySelected: (String) -> Unit) {
        countryName.text = country.name
        itemView.setOnClickListener { onCountrySelected(country.name) }
    }
}
