package ru.practicum.android.diploma.filter.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.filter.data.dto.IndustryDto

class IndustryAdapter(
    private val onClick: (String, Int) -> Unit
) : RecyclerView.Adapter<IndustryAdapter.IndustryViewHolder>() {

    private var industries = listOf<IndustryDto>()
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    // Обновление списка
    fun update(newIndustries: List<IndustryDto>) {
        industries = newIndustries
        notifyDataSetChanged()
    }

    fun setSelectedIndustryId(industryId: String) {
        selectedPosition = industries.indexOfFirst { it.id == industryId }
        notifyDataSetChanged()
    }

    class IndustryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val industryName: TextView = itemView.findViewById(R.id.industryName)
        private val industryRadioBtn: RadioButton = itemView.findViewById(R.id.industryRadioBtn)
        fun bind(industry: IndustryDto, isSelected: Boolean) {
            industryName.text = industry.name
            industryRadioBtn.isChecked = isSelected
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndustryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_industry, parent, false)
        return IndustryViewHolder(view)
    }

    fun getSelectedIndustry(): IndustryDto? {
        return if (selectedPosition != RecyclerView.NO_POSITION) {
            industries[selectedPosition]
        } else {
            null
        }
    }

    override fun onBindViewHolder(holder: IndustryViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val industry = industries[position]
        val isSelected = selectedPosition == position
        holder.bind(industry, isSelected)

        holder.itemView.setOnClickListener {
            if (selectedPosition != position) {
                val previousPosition = selectedPosition
                selectedPosition = position

                notifyItemChanged(previousPosition)
                notifyItemChanged(position)
                onClick(industry.id, position)
            }
        }
    }

    override fun getItemCount(): Int = industries.size
}
