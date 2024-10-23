package ru.practicum.android.diploma.filter.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.android.ext.android.inject
import ru.practicum.android.diploma.databinding.FragmentIndustryBinding
import ru.practicum.android.diploma.filter.domain.models.IndustryViewModel
import ru.practicum.android.diploma.filter.ui.FilterFragment.Companion.SELECTED_INDUSTRY_ID
import ru.practicum.android.diploma.search.presentation.models.UiScreenState

class IndustryFragment : Fragment() {

    private var _binding: FragmentIndustryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: IndustryViewModel by inject()

    private val industryAdapter: IndustryAdapter by lazy {
        IndustryAdapter { industryId, position ->
            binding.buttonChoose.visibility = View.VISIBLE
            binding.industryList.smoothScrollToPosition(position)
        }
    }

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

        val selectedIndustryId = arguments?.getString(SELECTED_INDUSTRY_ID).toString()

        addListeners()

        binding.industryList.adapter = industryAdapter
        binding.industryList.layoutManager = LinearLayoutManager(requireContext())

        viewModel.industriesList.observe(viewLifecycleOwner) { industries ->
            if (viewModel.isLoadingAllIndustries.value == true) {
                industryAdapter.update(industries)
                binding.industryList.visibility = View.VISIBLE
                binding.placeholderNoListIndustry.visibility = View.GONE
                binding.industryNoInternet.visibility = View.GONE
                viewModel.onIndustriesLoaded()
            } else if (industries.isNullOrEmpty()) {
                showNoListIndustry()
            } else {
                industryAdapter.update(industries)
                binding.industryList.visibility = View.VISIBLE
                binding.placeholderNoListIndustry.visibility = View.GONE
                binding.industryNoInternet.visibility = View.GONE
                selectedIndustryId.let {
                    industryAdapter.setSelectedIndustryId(it)
                }
            }
        }
        observeUiState()
        viewModel.loadAllIndustries()
    }

    private fun addListeners() {
        setFilterIndustryClear()

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

        binding.editIndustry.addTextChangedListener(object : TextWatcher {
            @Suppress("EmptyFunctionBlock")
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                viewModel.onSearchQueryChanged(query)
                binding.imageButtonIndustrySearch.visibility = View.GONE
                binding.imageButtonFilterIndustryClear.visibility = View.VISIBLE
            }

            @Suppress("EmptyFunctionBlock")
            override fun afterTextChanged(s: Editable?) {
            }
        })

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

    private fun setFilterIndustryClear() {
        binding.imageButtonFilterIndustryClear.setOnClickListener {
            binding.editIndustry.text.clear()
            viewModel.loadAllIndustries()
            binding.imageButtonFilterIndustryClear.visibility = View.GONE
            binding.imageButtonIndustrySearch.visibility = View.VISIBLE
        }
    }

    private fun showErrorIndustry() {
        binding.progressBar.visibility = View.GONE
        binding.industryList.visibility = View.GONE
        binding.placeholderErrorIndustry.visibility = View.VISIBLE
        binding.placeholderNoListIndustry.visibility = View.GONE
        binding.industryNoInternet.visibility = View.GONE
    }

    private fun showNoInternetIndustry() {
        binding.progressBar.visibility = View.GONE
        binding.industryList.visibility = View.GONE
        binding.placeholderErrorIndustry.visibility = View.GONE
        binding.placeholderNoListIndustry.visibility = View.GONE
        binding.industryNoInternet.visibility = View.VISIBLE
    }

    private fun showNoListIndustry() {
        binding.progressBar.visibility = View.GONE
        binding.industryList.visibility = View.GONE
        binding.placeholderNoListIndustry.visibility = View.VISIBLE
        binding.placeholderErrorIndustry.visibility = View.GONE
        binding.industryNoInternet.visibility = View.GONE
    }

    private fun observeUiState() {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiScreenState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.placeholderNoListIndustry.visibility = View.GONE
                    binding.placeholderErrorIndustry.visibility = View.GONE
                    binding.industryNoInternet.visibility = View.GONE
                }

                is UiScreenState.Default -> {
                    binding.progressBar.visibility = View.GONE
                }

                is UiScreenState.Empty -> {
                    viewModel.loadAllIndustries()
                }

                is UiScreenState.NoInternetError -> {
                    showNoInternetIndustry()

                }

                is UiScreenState.ServerError -> {
                    showErrorIndustry()
                }

                is UiScreenState.Success -> TODO()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
