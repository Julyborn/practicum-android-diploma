package ru.practicum.android.diploma.filter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFiltersBinding
import ru.practicum.android.diploma.filter.data.dto.FilterSettings
import ru.practicum.android.diploma.filter.presentation.FilterViewModel

class FilterFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!

    private val filterViewModel: FilterViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.arrowBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val savedFilters = filterViewModel.loadFilters()
        binding.addNameFilterJob.setText(savedFilters.location)
        binding.addNameFilterIndustry.setText(savedFilters.industry)
        updateIndustryVisibility(savedFilters)
        updateLocationVisibility(savedFilters)

        binding.editSalary.setText(savedFilters.salary)
        binding.checkBox2.isChecked = savedFilters.hideWithoutSalary

        showButtonsIfChanged()

        binding.addNameFilterJob.addTextChangedListener { showButtonsIfChanged() }
        binding.addNameFilterIndustry.addTextChangedListener { showButtonsIfChanged() }
        binding.editSalary.addTextChangedListener { showButtonsIfChanged() }
        binding.checkBox2.setOnCheckedChangeListener { _, _ -> showButtonsIfChanged() }

        setApplyButtonListener(savedFilters)
        setFragmentResultListeners(savedFilters)

        binding.imageButtonIndustry.setOnClickListener {
            openIndustryFragment()
        }
        binding.imageButtonJob.setOnClickListener {
            openWorkplaceFragment()
        }

        binding.imageButtonIndustryClear.setOnClickListener {
            clearIndustryFilter(savedFilters)
        }

        binding.imageButtonJobClear.setOnClickListener {
            clearJobFilter(savedFilters)
        }

        binding.imageButtonFilterSalaryClear.setOnClickListener {
            clearSalaryFilter()
        }

        setSalaryFocusChangeListener()
        setResetFilters(savedFilters)
    }

    private fun updateIndustryVisibility(savedFilters: FilterSettings) {
        if (savedFilters.industry != null) {
            binding.editIndustry.visibility = View.GONE
            binding.imageButtonIndustry.visibility = View.GONE
            binding.addFilterIndustryLinearLayout.visibility = View.VISIBLE
            binding.imageButtonIndustryClear.visibility = View.VISIBLE
        }
    }

    private fun updateLocationVisibility(savedFilters: FilterSettings) {
        if (savedFilters.location != null) {
            binding.addNameFilterJob.setText(savedFilters.location)
            binding.imageButtonJob.visibility = View.GONE
            binding.editJob.visibility = View.GONE
            binding.imageButtonJobClear.visibility = View.VISIBLE
            binding.addFilterJobLinearLayout.visibility = View.VISIBLE
            binding.addFilterJob.visibility = View.VISIBLE
            binding.addNameFilterJob.visibility = View.VISIBLE
        }
    }

    private fun setApplyButtonListener(savedFilters: FilterSettings) {
        binding.buttonApply.setOnClickListener {
            val location = binding.addNameFilterJob.text.toString().takeIf { it.isNotEmpty() }
            val industry = binding.addNameFilterIndustry.text.toString().takeIf { it.isNotEmpty() }
            val salary = binding.editSalary.text.toString().takeIf { it.isNotEmpty() }
            val industryId = savedFilters.industryId
            val hideWithoutSalary = binding.checkBox2.isChecked
            val selectedCountry = savedFilters.selectedCountry
            val selectedRegion = savedFilters.selectedRegion
            filterViewModel.applyFilters(
                location,
                industry,
                salary,
                industryId,
                selectedCountry,
                selectedRegion,
                hideWithoutSalary
            )
            parentFragmentManager.popBackStack()
        }
    }

    private fun setFragmentResultListeners(savedFilters: FilterSettings) {
        parentFragmentManager.setFragmentResultListener("industryRequestKey", this) { _, bundle ->
            val selectedIndustry = bundle.getString("selectedIndustry")
            val selectedIndustryId = bundle.getString("selectedIndustryId")
            if (selectedIndustry != null && selectedIndustryId != null) {
                binding.editIndustry.visibility = View.GONE
                binding.imageButtonIndustry.visibility = View.GONE
                savedFilters.industryId = selectedIndustryId
                binding.addNameFilterIndustry.text = selectedIndustry
                binding.addFilterIndustryLinearLayout.visibility = View.VISIBLE
                binding.imageButtonIndustryClear.visibility = View.VISIBLE
            }
        }
        parentFragmentManager.setFragmentResultListener("workplaceRequestKey", this) { _, bundle ->
            val selectedCountry = bundle.getString("selectedCountry")
            val selectedRegion = bundle.getString("selectedRegion")

            if (selectedCountry != null || selectedRegion != null) {
                val locationText = if (selectedRegion != null) {
                    "$selectedCountry, $selectedRegion"
                } else {
                    "$selectedCountry"
                }
                binding.addNameFilterJob.text = locationText
                savedFilters.selectedCountry = selectedCountry
                savedFilters.selectedRegion = selectedRegion
                binding.imageButtonJob.visibility = View.GONE
                binding.editJob.visibility = View.GONE
                binding.imageButtonJobClear.visibility = View.VISIBLE
                binding.addFilterJobLinearLayout.visibility = View.VISIBLE
                binding.addFilterJob.visibility = View.VISIBLE
                binding.addNameFilterJob.visibility = View.VISIBLE
            }
        }
    }

    private fun setResetFilters(savedFilters: FilterSettings) {
        binding.buttonResetFilter.setOnClickListener {
            filterViewModel.clearFilters()
            binding.addNameFilterJob.text = ""
            binding.addNameFilterIndustry.text = ""
            binding.editSalary.text.clear()
            binding.checkBox2.isChecked = false
            savedFilters.industry = null
            savedFilters.location = null
            savedFilters.salary = null
            savedFilters.hideWithoutSalary = false
            binding.buttonApply.visibility = View.GONE
            binding.buttonResetFilter.visibility = View.GONE
            binding.imageButtonFilterSalaryClear.visibility = View.GONE
            binding.addFilterJob.visibility = View.GONE
            binding.addNameFilterJob.visibility = View.GONE
            binding.editJob.visibility = View.VISIBLE
            binding.imageButtonJob.visibility = View.VISIBLE
            binding.addFilterIndustryLinearLayout.visibility = View.GONE
            binding.imageButtonIndustryClear.visibility = View.GONE
            binding.editIndustry.visibility = View.VISIBLE
            binding.imageButtonIndustry.visibility = View.VISIBLE
            binding.imageButtonJobClear.visibility = View.GONE
        }
    }

    private fun clearIndustryFilter(savedFilters: FilterSettings) {
        binding.addFilterIndustryLinearLayout.visibility = View.GONE
        binding.imageButtonIndustryClear.visibility = View.GONE
        binding.editIndustry.visibility = View.VISIBLE
        binding.imageButtonIndustry.visibility = View.VISIBLE
        binding.addNameFilterIndustry.text = ""
        savedFilters.industry = null
    }

    private fun clearJobFilter(savedFilters: FilterSettings) {
        binding.imageButtonJob.visibility = View.VISIBLE
        binding.editJob.visibility = View.VISIBLE
        binding.imageButtonJobClear.visibility = View.GONE
        binding.addNameFilterJob.text = ""
        savedFilters.location = null
    }

    private fun clearSalaryFilter() {
        binding.editSalary.text.clear()
        binding.editSalary.clearFocus()
        binding.expectedSalary.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
        binding.imageButtonFilterSalaryClear.visibility = View.GONE
    }

    private fun setSalaryFocusChangeListener() {
        binding.editSalary.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                binding.imageButtonFilterSalaryClear.visibility = View.GONE
                binding.expectedSalary.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            } else {
                binding.editSalary.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                binding.expectedSalary.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
                binding.imageButtonFilterSalaryClear.visibility = View.VISIBLE
            }
        }
    }

    private fun showButtonsIfChanged() {
        val isChanged = binding.addNameFilterJob.text.isNotEmpty() ||
            binding.addNameFilterIndustry.text.isNotEmpty() ||
            binding.editSalary.text.isNotEmpty() ||
            binding.checkBox2.isChecked

        if (isChanged) {
            binding.buttonApply.visibility = View.VISIBLE
            binding.buttonResetFilter.visibility = View.VISIBLE
        } else {
            binding.buttonApply.visibility = View.GONE
            binding.buttonResetFilter.visibility = View.GONE
        }
    }

    private fun openIndustryFragment() {
        findNavController().navigate(R.id.action_filterFragment_to_industryFragment)
    }

    private fun openWorkplaceFragment() {
        findNavController().navigate(R.id.action_filterFragment_to_workplaceFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
