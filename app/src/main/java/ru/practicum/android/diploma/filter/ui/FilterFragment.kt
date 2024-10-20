package ru.practicum.android.diploma.filter.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
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

        setupUI()
        observeViewModel()

        filterViewModel.loadFilters()
        showActionButtons()
        setFragmentResultListeners()
    }

    private fun setupUI() {
        binding.arrowBack.setOnClickListener { parentFragmentManager.popBackStack() }

        binding.editSalary.addTextChangedListener { salary ->
            filterViewModel.updateSalary(salary.toString())
            showActionButtons()

        }

        binding.checkBoxNoSalary.setOnCheckedChangeListener { _, isChecked ->
            filterViewModel.updateHideWithoutSalary(isChecked)
            showActionButtons()
        }

        setClickListeners()
        setFocusListeners()
    }

    private fun setClickListeners() {
        binding.buttonApply.setOnClickListener {
            filterViewModel.applyFilters()
            parentFragmentManager.popBackStack()
        }

        binding.buttonResetFilter.setOnClickListener {
            filterViewModel.clearFilters()
            resetFiltersUI()
        }

        binding.imageButtonIndustry.setOnClickListener { openIndustryFragment() }
        binding.imageButtonJob.setOnClickListener { openWorkplaceFragment() }

        binding.imageButtonIndustryClear.setOnClickListener { clearIndustryFilter() }
        binding.imageButtonJobClear.setOnClickListener { clearJobFilter() }
        binding.imageButtonFilterSalaryClear.setOnClickListener { clearSalaryFilter() }
    }

    private fun setFocusListeners() {
        binding.filterSalary.setOnClickListener {
            if (!binding.editSalary.hasFocus()) {
                binding.editSalary.requestFocus()
                showKeyboard(binding.editSalary)
            }
        }

        binding.editSalary.setOnFocusChangeListener { _, hasFocus ->
            updateSalaryVisibility(hasFocus)
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
                showActionButtons()
            }
        }

        filterViewModel.hideWithoutSalary.observe(viewLifecycleOwner) { hideWithoutSalary ->
            binding.checkBoxNoSalary.isChecked = hideWithoutSalary
        }

        filterViewModel.location.observe(viewLifecycleOwner) { location ->
            if (!location.isNullOrEmpty()) {
                binding.addNameFilterJob.text = location
                toggleJobVisibility(true)
            }
        }

        filterViewModel.industry.observe(viewLifecycleOwner) { industry ->
            binding.addNameFilterIndustry.text = industry
            toggleIndustryVisibility(!industry.isNullOrEmpty())
        }
    }

    private fun setFragmentResultListeners() {
        parentFragmentManager.setFragmentResultListener("industryRequestKey", this) { _, bundle ->
            val selectedIndustry = bundle.getString("selectedIndustry")
            if (selectedIndustry != null) {
                filterViewModel.updateIndustry(selectedIndustry)
                showActionButtons()
            }
        }

        parentFragmentManager.setFragmentResultListener("workplaceRequestKey", this) { _, bundle ->
            val selectedCountry = bundle.getString("selectedCountry")
            val selectedRegion = bundle.getString("selectedRegion")
            val locationText = listOfNotNull(selectedCountry, selectedRegion).joinToString(", ")

            filterViewModel.updateLocation(locationText)
            showActionButtons()
        }
    }


    private fun showActionButtons() {
        if (checkIfAnyFieldFilled()) {
            binding.buttonApply.visibility = View.VISIBLE
            binding.buttonResetFilter.visibility = View.VISIBLE
        } else {
            binding.buttonApply.visibility = View.GONE
            binding.buttonResetFilter.visibility = View.GONE
        }
    }

    private fun checkIfAnyFieldFilled(): Boolean {
        return binding.addNameFilterJob.text.isNotEmpty() ||
            binding.addNameFilterIndustry.text.isNotEmpty() ||
            binding.editSalary.text.isNotEmpty() ||
            binding.checkBoxNoSalary.isChecked
    }

    private fun resetFiltersUI() {
        binding.buttonApply.visibility = View.GONE
        binding.buttonResetFilter.visibility = View.GONE
        clearSalaryFilter()
        toggleJobVisibility(false)
        toggleIndustryVisibility(false)
    }

    private fun clearIndustryFilter() {
        toggleIndustryVisibility(false)
        filterViewModel.clearIndustry()
    }

    private fun clearJobFilter() {
        toggleJobVisibility(false)
        filterViewModel.clearJobFilter()
    }

    private fun clearSalaryFilter() {
        binding.editSalary.text.clear()
        binding.imageButtonFilterSalaryClear.visibility = View.GONE
        binding.expectedSalary.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
        binding.expectedSalary.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
        binding.editSalary.clearFocus()
    }

    private fun updateSalaryVisibility(hasFocus: Boolean) {
        binding.imageButtonFilterSalaryClear.visibility = if (hasFocus) View.VISIBLE else View.GONE
        val colorRes = if (hasFocus) R.color.blue else R.color.black
        binding.expectedSalary.setTextColor(ContextCompat.getColor(requireContext(), colorRes))
    }

    private fun toggleIndustryVisibility(isVisible: Boolean) {
        binding.addFilterIndustryLinearLayout.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.imageButtonIndustryClear.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.editIndustry.visibility = if (isVisible) View.GONE else View.VISIBLE
        binding.imageButtonIndustry.visibility = if (isVisible) View.GONE else View.VISIBLE
    }

    private fun toggleJobVisibility(isVisible: Boolean) {
        binding.addFilterJobLinearLayout.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.imageButtonJobClear.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.editJob.visibility = if (isVisible) View.GONE else View.VISIBLE
        binding.imageButtonJob.visibility = if (isVisible) View.GONE else View.VISIBLE
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
