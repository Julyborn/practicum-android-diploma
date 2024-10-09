package ru.practicum.android.diploma.filter.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentWorkplaceBinding
import ru.practicum.android.diploma.filter.presentation.WorkplaceViewModel

class WorkplaceFragment : Fragment() {

    private var _binding: FragmentWorkplaceBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<WorkplaceViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkplaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        parentFragmentManager.setFragmentResultListener(COUNTRY_KEY, this) { key, bundle ->
            val selectedCountry = bundle.getString(SELECTED_COUNTRY)
            viewModel.selectCountry(selectedCountry ?: DEF)
        }
        viewModel.selectedCountry.observe(viewLifecycleOwner) { country ->
            binding.countryEditText.setText(country)
            changeHintColor(binding.countryTextInputLayout, country != null)
        }
        viewModel.isCountrySelected.observe(viewLifecycleOwner) { isSelected ->
            if (isSelected) {
                binding.icCountryArrow.setImageResource(R.drawable.ic_clear)
            } else {
                binding.icCountryArrow.setImageResource(R.drawable.ic_right_arrow)
            }
        }
        binding.countryEditText.setOnClickListener {
            navController.navigate(R.id.action_workplaceFragment_to_countryChoosingFragment)
        }
        binding.icCountryArrow.setOnClickListener {
            if (viewModel.isCountrySelected.value == true) {
                viewModel.clearCountry()
            }
        }
        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        changeHintColor(binding.countryTextInputLayout, false)
    }

    private fun changeHintColor(inputLayout: TextInputLayout, isSelected: Boolean) {
        if (isSelected) {
            inputLayout.defaultHintTextColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black_for_hint))
        } else {
            inputLayout.defaultHintTextColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.grey))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val SELECTED_COUNTRY = "selected_country"
        const val COUNTRY_KEY = "countryRequestKey"
        const val DEF = ""
    }
}
