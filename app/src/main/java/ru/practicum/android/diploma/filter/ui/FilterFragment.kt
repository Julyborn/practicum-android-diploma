package ru.practicum.android.diploma.filter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFiltersBinding
import ru.practicum.android.diploma.filter.presentation.FilterViewModel
import ru.practicum.android.diploma.search.presentation.SearchViewModel

class FilterFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel by viewModel<SearchViewModel>()
    private val filterViewModel by viewModel<FilterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val savedFilters = filterViewModel.loadFilters()
        binding.editJob.setText(savedFilters.location)
        binding.editIndustry.setText(savedFilters.industry)
        binding.editSalary.setText(savedFilters.salary)
        binding.checkBox2.isChecked = savedFilters.hideWithoutSalary

        binding.arrowBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.buttonApply.setOnClickListener {
            val location = binding.editJob.text.toString().takeIf { it.isNotEmpty() }
            val industry = binding.editIndustry.text.toString().takeIf { it.isNotEmpty() }
            val salary = binding.editSalary.text.toString().takeIf { it.isNotEmpty() }
            val hideWithoutSalary = binding.checkBox2.isChecked

            filterViewModel.applyFilters(location, industry, salary, hideWithoutSalary)

            val bundle = bundleOf(
                "location" to location,
                "industry" to industry,
                "salary" to salary,
                "hideWithoutSalary" to hideWithoutSalary
            )

            parentFragmentManager.setFragmentResult("filterRequestKey", bundle)
            parentFragmentManager.popBackStack()
        }

        binding.imageButtonIndustry.setOnClickListener {
            openIndustryFragment()
        }

        binding.imageButtonIndustryClear.setOnClickListener {
            binding.addFilterIndustryLinearLayout.visibility = View.GONE
            binding.imageButtonIndustryClear.visibility = View.GONE
            binding.editIndustry.visibility = View.VISIBLE
            binding.imageButtonIndustry.visibility = View.VISIBLE
            binding.addNameFilterIndustry.text = ""
        }

        binding.imageButtonFilterSalaryClear.setOnClickListener {
            binding.editSalary.text.clear()
            binding.editSalary.clearFocus()
            binding.imageButtonFilterSalaryClear.visibility = View.GONE
        }
    }

    private fun openIndustryFragment() {
        findNavController().navigate(R.id.action_filterFragment_to_industryFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
