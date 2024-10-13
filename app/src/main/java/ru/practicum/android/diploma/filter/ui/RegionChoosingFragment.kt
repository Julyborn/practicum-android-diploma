package ru.practicum.android.diploma.filter.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentRegionChoosingBinding
import ru.practicum.android.diploma.filter.domain.models.Region
import ru.practicum.android.diploma.filter.presentation.RegionChoosingViewModel
import ru.practicum.android.diploma.filter.presentation.WorkplaceState

class RegionChoosingFragment : Fragment() {

    private var _binding: FragmentRegionChoosingBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<RegionChoosingViewModel>()

    private val regionAdapter: RegionAdapter = RegionAdapter { region -> onRegionSelected(region) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegionChoosingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.regionsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.regionsRecyclerView.adapter = regionAdapter
        binding.regionsRecyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.loadRegions()
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is WorkplaceState.Loading -> showLoadingIndicator()
                is WorkplaceState.Success -> showSuccess(state.regions ?: emptyList())
                is WorkplaceState.FetchError -> showFetchError()
                is WorkplaceState.NoRegionsError -> showNoRegionsError()
            }

        }
        binding.regionsEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filterRegions(s.toString())
                if (s.isNullOrEmpty()) {
                    binding.icSearch.visibility = View.VISIBLE
                    binding.icClear.visibility = View.GONE
                } else {
                    binding.icSearch.visibility = View.GONE
                    binding.icClear.visibility = View.VISIBLE
                }
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })

        binding.icClear.setOnClickListener {
            binding.regionsEditText.text?.clear()
        }

        binding.backButton.setOnClickListener { findNavController().popBackStack() }
    }

    private fun showLoadingIndicator() {
        binding.placeholderError.visibility = View.GONE
        binding.placeholderNoList.visibility = View.GONE
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.regionsRecyclerView.visibility = View.GONE
    }

    private fun showSuccess(regions: List<Region>) {
        binding.loadingIndicator.visibility = View.GONE
        binding.placeholderError.visibility = View.GONE
        binding.placeholderNoList.visibility = View.GONE
        regionAdapter.update(regions)
        binding.regionsRecyclerView.visibility = View.VISIBLE
    }

    private fun showNoRegionsError() {
        binding.loadingIndicator.visibility = View.GONE
        binding.regionsRecyclerView.visibility = View.GONE
        binding.placeholderError.visibility = View.GONE
        binding.placeholderNoList.visibility = View.VISIBLE
    }

    private fun showFetchError() {
        binding.loadingIndicator.visibility = View.GONE
        binding.regionsRecyclerView.visibility = View.GONE
        binding.placeholderNoList.visibility = View.GONE
        binding.placeholderError.visibility = View.VISIBLE
    }

    private fun onRegionSelected(region: Region) {
        viewModel.selectRegion(region)
        requireActivity().onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
