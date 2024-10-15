package ru.practicum.android.diploma.filter.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFiltersBinding
import ru.practicum.android.diploma.filter.presentation.FilterViewModel

class FilterFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!

    private val filterViewModel:FilterViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        binding.editJob.setText(savedFilters.location)
        binding.editIndustry.setText(savedFilters.industry)
        binding.editSalary.setText(savedFilters.salary)
        binding.checkBox2.isChecked = savedFilters.hideWithoutSalary

        binding.buttonApply.setOnClickListener {
            val location = binding.addNameFilterJob.text.toString().takeIf { it.isNotEmpty() }
            val industry = binding.addNameFilterIndustry.text.toString().takeIf { it.isNotEmpty() }
            val salary = binding.editSalary.text.toString().takeIf { it.isNotEmpty() }
            val hideWithoutSalary = binding.checkBox2.isChecked

            filterViewModel.applyFilters(location, industry, salary, hideWithoutSalary)
            Log.d("applyFilters","applyFilters ${filterViewModel.applyFilters(location, industry, salary, hideWithoutSalary)}")
            parentFragmentManager.popBackStack()
        }

        parentFragmentManager.setFragmentResultListener("industryRequestKey", this) { _, bundle ->
            val selectedIndustry = bundle.getString("selectedIndustry")
            if (selectedIndustry != null) {
                binding.editIndustry.visibility = View.GONE
                binding.imageButtonIndustry.visibility = View.GONE

                binding.addNameFilterIndustry.text = selectedIndustry
                binding.addFilterIndustryLinearLayout.visibility = View.VISIBLE
                binding.imageButtonIndustryClear.visibility = View.VISIBLE
            }
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

        binding.imageButtonFilterSalaryClear.setOnClickListener{
            binding.editSalary.text.clear()
            binding.editSalary.clearFocus()
            binding.expectedSalary.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
            binding.imageButtonFilterSalaryClear.visibility = View.GONE
        }

        binding.buttonResetFilter.setOnClickListener {
            filterViewModel.clearFilters()
            binding.editJob.text = ""
            binding.editIndustry.text = ""
            binding.editSalary.text.clear()
            binding.checkBox2.isChecked = false
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
