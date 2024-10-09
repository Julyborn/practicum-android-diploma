package ru.practicum.android.diploma.filter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentCountryChoosingBinding
import ru.practicum.android.diploma.filter.presentation.CountryState
import ru.practicum.android.diploma.filter.presentation.CountryViewModel

class CountryChoosingFragment : Fragment() {

    private var _binding: FragmentCountryChoosingBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<CountryViewModel>()
    private lateinit var adapter: CountryAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCountryChoosingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CountryAdapter { country ->
            onCountrySelected(country)
        }
        binding.countryRV.adapter = adapter
        binding.countryRV.layoutManager = LinearLayoutManager(context)
        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        viewModel.countries.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CountryState.Success -> {
                    adapter.update(state.countries)
                }

                is CountryState.Error -> {
                    binding.noCountryPlaceholder.visibility = View.VISIBLE
                    binding.countryRV.visibility = View.GONE
                }
            }
        }
        viewModel.loadCountries()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onCountrySelected(country: String) {
        val resultBundle = Bundle().apply {
            putString(SELECTED_COUNTRY, country)
        }
        parentFragmentManager.setFragmentResult(COUNTRY_KEY, resultBundle)
        requireActivity().onBackPressed()
    }

    companion object {
        const val SELECTED_COUNTRY = "selected_country"
        const val COUNTRY_KEY = "countryRequestKey"
    }
}
