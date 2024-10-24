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
import ru.practicum.android.diploma.filter.domain.models.Country
import ru.practicum.android.diploma.filter.presentation.CountryChoosingViewModel
import ru.practicum.android.diploma.filter.presentation.WorkplaceState

class CountryChoosingFragment : Fragment() {

    private var _binding: FragmentCountryChoosingBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<CountryChoosingViewModel>()
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

        val adapter = CountryAdapter { country ->
            onCountrySelected(country)
        }
        binding.countryRV.adapter = adapter
        binding.countryRV.layoutManager = LinearLayoutManager(context)
        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        viewModel.loadCountries()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is WorkplaceState.Loading -> {
                    binding.loadingIndicator.visibility = View.VISIBLE
                    binding.noCountryPlaceholder.visibility = View.GONE
                    binding.countryRV.visibility = View.GONE
                    binding.countryNoInternet.visibility = View.GONE
                }

                is WorkplaceState.Success -> {
                    binding.loadingIndicator.visibility = View.GONE
                    binding.noCountryPlaceholder.visibility = View.GONE
                    binding.countryRV.visibility = View.VISIBLE
                    binding.countryNoInternet.visibility = View.GONE
                    adapter.update(state.countries)
                }

                is WorkplaceState.FetchError -> {
                    binding.loadingIndicator.visibility = View.GONE
                    binding.noCountryPlaceholder.visibility = View.VISIBLE
                    binding.countryRV.visibility = View.GONE
                    binding.countryNoInternet.visibility = View.GONE
                }

                is WorkplaceState.NoInternet -> {
                    binding.loadingIndicator.visibility = View.GONE
                    binding.noCountryPlaceholder.visibility = View.GONE
                    binding.countryRV.visibility = View.GONE
                    binding.countryNoInternet.visibility = View.VISIBLE
                }

                else -> {
                    binding.loadingIndicator.visibility = View.GONE
                    binding.noCountryPlaceholder.visibility = View.VISIBLE
                    binding.countryRV.visibility = View.GONE
                    binding.countryNoInternet.visibility = View.GONE

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onCountrySelected(country: Country) {
        viewModel.selectCountry(country)
        requireActivity().onBackPressed()
    }
}
