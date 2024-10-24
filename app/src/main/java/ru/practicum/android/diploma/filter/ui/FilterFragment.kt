package ru.practicum.android.diploma.filter.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.arrowBack.setOnClickListener {
            filterViewModel.restoreWorkplace()
            parentFragmentManager.popBackStack()
        }

        // Добавляем TextWatcher для editSalary
        binding.editSalary.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterViewModel.updateSalary(s.toString())
                updateClearButtonVisibility()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ничего не делаем
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Ничего не делаем
            }
        })

        binding.checkBox2.setOnCheckedChangeListener { _, isChecked ->
            filterViewModel.updateHideWithoutSalary(isChecked)
        }

        observeViewModel()

        setApplyButtonListener()
        setFragmentResultListeners()

        binding.filterIndustry.setOnClickListener {
            openIndustryFragment()
        }

        binding.imageButtonIndustry.setOnClickListener {
            openIndustryFragment()
        }

        binding.filterJob.setOnClickListener {
            openWorkplaceFragment()
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

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    filterViewModel.restoreWorkplace()
                    findNavController().popBackStack()
                }
            }
        )

        setSalaryFocusChangeListener()
        setResetFilters()

        filterViewModel.isApplyButtonVisible.observe(viewLifecycleOwner) { isVisible ->
            binding.buttonApply.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
        filterViewModel.isResetButtonVisible.observe(viewLifecycleOwner) { isVisible ->
            binding.buttonResetFilter.visibility = if (isVisible) View.VISIBLE else View.GONE
        }

        setFocusListeners()
    }

    private fun setFocusListeners() {
        binding.filterSalary.setOnClickListener {
            binding.editSalary.requestFocus()
            showKeyboard(binding.editSalary)
        }
    }

    @SuppressLint("ServiceCast")
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
                filterViewModel.updateIndustry(
                    industryId = selectedIndustryId,
                    industry = selectedIndustry
                )
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
            binding.addFilterJobLinearLayout.visibility = View.GONE
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
            // Очищаем поле зарплаты
            binding.editSalary.setText("")
            updateClearButtonVisibility()
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
        binding.editJob.visibility = View.VISIBLE
        binding.imageButtonJob.visibility = View.VISIBLE
        binding.addNameFilterJob.visibility = View.VISIBLE
        filterViewModel.clearJobFilter()
    }

    private fun clearSalaryFilter() {
        filterViewModel.clearSalary()
        binding.editSalary.setText("")
        binding.editSalary.clearFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editSalary.windowToken, 0)
        binding.expectedSalary.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.salary_text)
        )
        updateClearButtonVisibility()
    }

    private fun setSalaryFocusChangeListener() {
        binding.editSalary.setOnFocusChangeListener { _, hasFocus ->
            updateClearButtonVisibility()
            if (!hasFocus) {
                binding.expectedSalary.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.black)
                )
            } else {
                binding.editSalary.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.black)
                )
                binding.expectedSalary.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.blue)
                )
            }
        }
    }

    private fun openIndustryFragment() {
        val selectedIndustryId: String? = filterViewModel.industryId.value
        val bundle = Bundle().apply {
            putString(SELECTED_INDUSTRY_ID, selectedIndustryId)
        }
        findNavController().navigate(
            R.id.action_filterFragment_to_industryFragment,
            bundle
        )
    }

    private fun openWorkplaceFragment() {
        findNavController().navigate(R.id.action_filterFragment_to_workplaceFragment)
    }

    private fun updateClearButtonVisibility() {
        val hasFocus = binding.editSalary.hasFocus()
        val isNotEmpty = !binding.editSalary.text.isNullOrEmpty()
        binding.imageButtonFilterSalaryClear.visibility =
            if (hasFocus && isNotEmpty) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val SELECTED_INDUSTRY_ID = "selectedIndustryId"
    }
}
