package ru.practicum.android.diploma.filter.ui

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.filter.domain.models.Region

class RegionViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
    private val regionName: TextView = rootView.findViewById(R.id.countryName)

    fun bind(region: Region, onRegionSelected: (Region) -> Unit) {
        regionName.text = region.name
        itemView.setOnClickListener { onRegionSelected(region) }
    }
}
