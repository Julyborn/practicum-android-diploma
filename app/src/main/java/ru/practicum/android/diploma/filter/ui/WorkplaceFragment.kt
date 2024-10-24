package ru.practicum.android.diploma.filter.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
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
        viewModel.loadSavedWorkplaceSettings()
        viewModel.selectedCountry.observe(viewLifecycleOwner) { country ->
            binding.countryEditText.setText(country?.name ?: DEF)
            changeHintColor(binding.countryTextInputLayout, country != null)
            binding.icCountryClear.visibility = if (country != null) View.VISIBLE else View.GONE
            binding.icCountryArrow.visibility = if (country != null) View.GONE else View.VISIBLE
            binding.chooseButton.visibility = if (country != null) View.VISIBLE else View.GONE
        }
        viewModel.selectedRegion.observe(viewLifecycleOwner) { region ->
            binding.regionEditText.setText(region?.name ?: DEF)
            changeHintColor(binding.regionTextInputLayout, region != null)
            binding.icRegionClear.visibility = if (region != null) View.VISIBLE else View.GONE
            binding.icRegionArrow.visibility = if (region != null) View.GONE else View.VISIBLE
        }

        clickListeners()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.restoreWorkplace()
                    findNavController().popBackStack()
                }
            }
        )
        changeHintColor(binding.countryTextInputLayout, false)
        changeHintColor(binding.regionTextInputLayout, false)
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

    private fun clickListeners() {
        binding.countryEditText.setOnClickListener {
            findNavController().navigate(R.id.action_workplaceFragment_to_countryChoosingFragment)
        }
        binding.icCountryArrow.setOnClickListener {
            findNavController().navigate(R.id.action_workplaceFragment_to_countryChoosingFragment)
        }
        binding.regionEditText.setOnClickListener {
            findNavController().navigate(R.id.action_workplaceFragment_to_regionChoosingFragment)
        }
        binding.icRegionArrow.setOnClickListener {
            findNavController().navigate(R.id.action_workplaceFragment_to_regionChoosingFragment)
        }
        binding.backButton.setOnClickListener {
            viewModel.restoreWorkplace()
            findNavController().popBackStack()
        }
        binding.icCountryClear.setOnClickListener { viewModel.clearCountry() }
        binding.icRegionClear.setOnClickListener { viewModel.clearRegion() }
        binding.chooseButton.setOnClickListener {
            val selectedCountry = viewModel.selectedCountry.value
            val selectedRegion = viewModel.selectedRegion.value

            if (selectedCountry != null || selectedRegion != null) {
                parentFragmentManager.setFragmentResult(
                    "workplaceRequestKey",
                    bundleOf(
                        "selectedCountry" to selectedCountry?.name,
                        "selectedCountryId" to selectedCountry?.id,
                        "selectedRegion" to selectedRegion?.name,
                        "selectedRegionId" to selectedRegion?.id
                    )
                )
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadSavedWorkplaceSettings()
    }

    companion object {
        const val DEF = ""
    }
}
