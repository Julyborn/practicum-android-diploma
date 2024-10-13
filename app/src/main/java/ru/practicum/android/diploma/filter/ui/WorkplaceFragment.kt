package ru.practicum.android.diploma.filter.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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

        viewModel.selectedCountry.observe(viewLifecycleOwner) { country ->
            binding.countryEditText.setText(country?.name ?: DEF)
            changeHintColor(binding.countryTextInputLayout, country != null)
            binding.icCountryArrow.setImageResource(
                if (country != null) R.drawable.ic_button_clear else R.drawable.ic_right_arrow
            )
            binding.chooseButton.visibility = if (country != null) View.VISIBLE else View.GONE
        }
        viewModel.selectedRegion.observe(viewLifecycleOwner) { region ->
            binding.regionEditText.setText(region?.name ?: DEF)
            changeHintColor(binding.regionTextInputLayout, region != null)
            binding.icRegionArrow.setImageResource(
                if (region != null) R.drawable.ic_button_clear else R.drawable.ic_right_arrow
            )
        }

        binding.countryEditText.setOnClickListener {
            navController.navigate(R.id.action_workplaceFragment_to_countryChoosingFragment)
        }
        binding.regionEditText.setOnClickListener {
            navController.navigate(R.id.action_workplaceFragment_to_regionChoosingFragment)
        }

        binding.backButton.setOnClickListener {
            viewModel.clearCountry()
            findNavController().popBackStack()
        }

        binding.icCountryArrow.setOnClickListener { viewModel.clearCountry() }
        binding.icRegionArrow.setOnClickListener { viewModel.clearRegion() }
        binding.chooseButton.setOnClickListener {findNavController().popBackStack() }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.clearCountry()
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
