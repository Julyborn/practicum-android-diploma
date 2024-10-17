package ru.practicum.android.diploma.filter.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.android.ext.android.inject
import ru.practicum.android.diploma.databinding.FragmentIndustryBinding
import ru.practicum.android.diploma.filter.domain.models.IndustryViewModel
import ru.practicum.android.diploma.search.presentation.models.UiScreenState

class IndustryFragment : Fragment() {

    private var _binding: FragmentIndustryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: IndustryViewModel by inject()

    private lateinit var industryAdapter: IndustryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIndustryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addListeners()

        industryAdapter = IndustryAdapter { industryId, position ->
            Log.d("IndustryFragment", "Industry selected: $industryId at position $position")
            binding.buttonChoose.visibility = View.VISIBLE
            binding.industryList.smoothScrollToPosition(position)
        }

        binding.industryList.adapter = industryAdapter
        binding.industryList.layoutManager = LinearLayoutManager(requireContext())

        viewModel.industriesList.observe(viewLifecycleOwner) { industries ->
            industryAdapter.update(industries)
        }

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiScreenState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is UiScreenState.Default -> {
                    binding.progressBar.visibility = View.GONE
                }

                is UiScreenState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "No industries found", Toast.LENGTH_SHORT).show()
                }

                is UiScreenState.NoInternetError -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
                }

                is UiScreenState.ServerError -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Server error", Toast.LENGTH_SHORT).show()
                }

                is UiScreenState.Success -> TODO()
            }
        }

        viewModel.onSearchQueryChanged("")

    }

    private fun addListeners() {
        setSearchButtonListeners()

        binding.arrowBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.industryList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                    if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                        viewModel.onLastItemReached()
                    }
                }
            }
        })

        binding.buttonChoose.setOnClickListener {
            val selectedIndustry = industryAdapter.getSelectedIndustry()
            if (selectedIndustry != null) {
                parentFragmentManager.setFragmentResult(
                    "industryRequestKey",
                    bundleOf(
                        "selectedIndustry" to selectedIndustry.name,
                        "selectedIndustryId" to selectedIndustry.id
                    )
                )
                parentFragmentManager.popBackStack()
            }
        }

        binding.editIndustry.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                binding.imageButtonFilterIndustryClear.visibility = View.GONE
                binding.imageButtonIndustrySearch.visibility = View.VISIBLE
            } else {
                binding.imageButtonFilterIndustryClear.visibility = View.VISIBLE
                binding.imageButtonIndustrySearch.visibility = View.GONE
            }
        }
    }

    private fun setSearchButtonListeners() {
        binding.imageButtonIndustrySearch.setOnClickListener {
            val query = binding.editIndustry.text.toString().trim()
            if (query.isNotEmpty()) {
                viewModel.onSearchQueryChanged(query)
                binding.imageButtonFilterIndustryClear.visibility = View.VISIBLE
                binding.imageButtonIndustrySearch.visibility = View.GONE
            } else {
                viewModel.onSearchQueryChanged("")
            }
        }

        binding.imageButtonFilterIndustryClear.setOnClickListener {
            binding.editIndustry.text.clear()
            viewModel.onSearchQueryChanged("")
            binding.imageButtonFilterIndustryClear.visibility = View.GONE
            binding.imageButtonIndustrySearch.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
