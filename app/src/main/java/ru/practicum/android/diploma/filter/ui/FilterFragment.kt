package ru.practicum.android.diploma.filter.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFiltersBinding
import ru.practicum.android.diploma.filter.presentation.FilterViewModel

class FilterFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!

    private val filterViewModel: FilterViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.arrowBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        filterViewModel.loadFilters()
        observeViewModel()
        showButtonsIfChanged()

        binding.checkBox2.setOnCheckedChangeListener { _, isChecked ->
            filterViewModel.updateHideWithoutSalary(isChecked)
            showButtonsIfChanged()
        }

        setApplyButtonListener()
        setFragmentResultListeners()

        binding.imageButtonIndustry.setOnClickListener {
            openIndustryFragment()
        }
        binding.imageButtonJob.setOnClickListener {
            openWorkplaceFragment()
        }

        binding.imageButtonIndustryClear.setOnClickListener {
            clearIndustryFilter()
        }

        binding.imageButtonJobClear.setOnClickListener {
            clearJobFilter()
        }

        binding.imageButtonFilterSalaryClear.setOnClickListener {
            clearSalaryFilter()
        }

        setSalaryFocusChangeListener()
        setResetFilters()
        setFocusListeners()
    }

    private fun setFocusListeners() {
        showButtonsIfChanged()
        binding.filterSalary.setOnClickListener {
            binding.editSalary.requestFocus()
            showKeyboard(binding.editSalary)
            binding.editSalary.addTextChangedListener { salary ->
                filterViewModel.updateSalary(salary.toString())
            }
        }
    }

  private fun showKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun observeViewModel() {
        filterViewModel.salary.observe(viewLifecycleOwner) { salary ->
            if (binding.editSalary.text.toString() != salary) {
                binding.editSalary.setText(salary)
                binding.editSalary.setSelection(salary.length)
            }
        }

        filterViewModel.hideWithoutSalary.observe(viewLifecycleOwner) { hideWithoutSalary ->
            binding.checkBox2.isChecked = hideWithoutSalary
        }

        filterViewModel.location.observe(viewLifecycleOwner) { location ->
            if (location.isNotEmpty()) {
                binding.addNameFilterJob.text = location
                updateLocationVisibility()
            }
        }

        filterViewModel.industry.observe(viewLifecycleOwner) { industry ->
            binding.addNameFilterIndustry.text = industry
            if (industry != null) {
                updateIndustryVisibility()
            }
        }
    }

    private fun updateIndustryVisibility() {
        binding.editIndustry.visibility = View.GONE
        binding.imageButtonIndustry.visibility = View.GONE
        binding.addFilterIndustryLinearLayout.visibility = View.VISIBLE
        binding.imageButtonIndustryClear.visibility = View.VISIBLE
    }

    private fun updateLocationVisibility() {
        binding.imageButtonJob.visibility = View.GONE
        binding.editJob.visibility = View.GONE
        binding.imageButtonJobClear.visibility = View.VISIBLE
        binding.addFilterJobLinearLayout.visibility = View.VISIBLE
        binding.addFilterJob.visibility = View.VISIBLE
        binding.addNameFilterJob.visibility = View.VISIBLE
    }

    private fun setApplyButtonListener() {
        binding.buttonApply.setOnClickListener {
            filterViewModel.applyFilters()
            parentFragmentManager.popBackStack()
        }
    }

    private fun setFragmentResultListeners() {
        parentFragmentManager.setFragmentResultListener("industryRequestKey", this) { _, bundle ->
            val selectedIndustry = bundle.getString("selectedIndustry")
            val selectedIndustryId = bundle.getString("selectedIndustryId")
            if (selectedIndustry != null && selectedIndustryId != null) {
                filterViewModel.updateIndustry(industryId = selectedIndustryId, industry = selectedIndustry)
                binding.editIndustry.visibility = View.GONE
                binding.imageButtonIndustry.visibility = View.GONE
                binding.addFilterIndustryLinearLayout.visibility = View.VISIBLE
                binding.imageButtonIndustryClear.visibility = View.VISIBLE

            }
        }
        parentFragmentManager.setFragmentResultListener("workplaceRequestKey", this) { _, bundle ->
            val selectedCountry = bundle.getString("selectedCountry")
            val selectedCountryId = bundle.getString("selectedCountryId")
            val selectedRegion = bundle.getString("selectedRegion")
            val selectedRegionId = bundle.getString("selectedRegionId")

            if (selectedCountry != null || selectedRegion != null) {
                val locationText = if (selectedRegion != null) {
                    "$selectedCountry, $selectedRegion"
                } else {
                    "$selectedCountry"
                }
                val areaId = selectedRegionId ?: selectedCountryId
                filterViewModel.setLocation(locationText)
                filterViewModel.setArea(areaId)
            }
        }
    }

    private fun setResetFilters() {
        binding.buttonResetFilter.setOnClickListener {
            filterViewModel.clearFilters()
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

    private fun clearIndustryFilter() {
        binding.addFilterIndustryLinearLayout.visibility = View.GONE
        binding.imageButtonIndustryClear.visibility = View.GONE
        binding.editIndustry.visibility = View.VISIBLE
        binding.imageButtonIndustry.visibility = View.VISIBLE
        filterViewModel.clearIndustry()
    }

    private fun clearJobFilter() {
        binding.addFilterJobLinearLayout.visibility = View.GONE
        binding.imageButtonJobClear.visibility = View.GONE
        binding.imageButtonJob.visibility = View.VISIBLE
        binding.addNameFilterJob.visibility = View.VISIBLE
        binding.editJob.visibility = View.VISIBLE
        filterViewModel.clearJobFilter()
    }

    private fun clearSalaryFilter() {
        filterViewModel.clearSalary()
        binding.editSalary.clearFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editSalary.windowToken, 0)
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
        val isChanged =
            binding.addNameFilterJob.text.isNotEmpty() || binding.addNameFilterIndustry.text.isNotEmpty() || binding.editSalary.text.isNotEmpty() || binding.checkBox2.isChecked

        binding.buttonApply.visibility = View.VISIBLE
        binding.buttonResetFilter.visibility = View.VISIBLE

//        if (isChanged) {
//            binding.buttonApply.visibility = View.VISIBLE
//            binding.buttonResetFilter.visibility = View.VISIBLE
//        } else {
//            binding.buttonApply.visibility = View.GONE
//            binding.buttonResetFilter.visibility = View.GONE
//        }
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
