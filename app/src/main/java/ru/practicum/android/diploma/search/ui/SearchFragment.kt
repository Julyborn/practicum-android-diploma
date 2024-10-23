package ru.practicum.android.diploma.search.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.filter.domain.api.FilterInteractor
import ru.practicum.android.diploma.search.presentation.SearchViewModel
import ru.practicum.android.diploma.search.presentation.models.UiScreenState
import ru.practicum.android.diploma.search.presentation.models.VacancyUi
import ru.practicum.android.diploma.util.debounce

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<SearchViewModel>()
    private var searchItemAdapter: SearchItemAdapter? = null
    private val debounceSearch = debounce<String>(
        delayMs = 2000L,
        scope = lifecycleScope,
        action = { query ->
            viewModel.onSearchQueryChanged(query)
        }
    )
    private val filterInteractor: FilterInteractor by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiState.observe(viewLifecycleOwner) {
            renderUiState(it)
        }

        binding.searchEditText.addTextChangedListener { query ->
            debounceSearch(query.toString())
        }

        binding.searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.searchEditText.text.toString()
                debounceSearch.cancel()
                viewModel.onSearchQueryChanged(query)
                true
            } else {
                false
            }
        }

        binding.clearIcon.setOnClickListener {
            binding.searchEditText.text.clear()
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            @Suppress("EmptyFunctionBlock")
            override fun afterTextChanged(s: Editable?) {
            }
            @Suppress("EmptyFunctionBlock")
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.searchIcon.visibility = View.VISIBLE
                    binding.clearIcon.visibility = View.GONE
                    debounceSearch.cancel()
                    viewModel.onSearchQueryChanged("")
                } else {
                    binding.searchIcon.visibility = View.GONE
                    binding.clearIcon.visibility = View.VISIBLE
                }
            }
        })

        binding.filterButton.setOnClickListener {
            openFilterFragment()
        }

        handleError()
        setupRecyclerView()
        observePaginationLoading()
    }

    override fun onResume() {
        super.onResume()
        highlightFilterButton(hasActiveFilters())
    }

    private fun renderUiState(state: UiScreenState) {
        when (state) {
            UiScreenState.Default -> showDefaultState()
            UiScreenState.Empty -> showEmptyState()
            UiScreenState.Loading -> showLoadingState()
            UiScreenState.NoInternetError -> {
                if (viewModel.isFirstSearch) {
                    showNoInternetErrorState()
                }
            }
            UiScreenState.ServerError -> {
                if (viewModel.isFirstSearch) {
                    showServerErrorState()
                }
            }
            is UiScreenState.Success -> showSuccessState(state.vacancies, state.found)
        }
    }

    private fun showDefaultState() {
        hideAllState()
        binding.ivImgSearch.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        hideAllState()
        binding.tvCountList.text = getString(R.string.no_vacancies_text)
        binding.tvCountList.visibility = View.VISIBLE
        binding.clEmptyList.visibility = View.VISIBLE
    }

    private fun showLoadingState() {
        hideAllState()
        binding.pbLoading.visibility = View.VISIBLE
    }

    private fun showNoInternetErrorState() {
        hideAllState()
        binding.clNoInternet.visibility = View.VISIBLE
    }

    @Suppress("EmptyBlock")
    private fun showServerErrorState() {
        // Implement this method later
    }

    private fun showSuccessState(vacancies: List<VacancyUi>, found: Int) {
        hideAllState()
        searchItemAdapter?.update(vacancies)
        binding.tvCountList.text = resources.getQuantityString(R.plurals.found_vacancies_count, found, found)
        binding.tvCountList.visibility = View.VISIBLE
        binding.vacancyList.visibility = View.VISIBLE
    }

    private fun hideAllState() {
        binding.ivImgSearch.visibility = View.GONE
        binding.tvCountList.visibility = View.GONE
        binding.vacancyList.visibility = View.GONE
        binding.pbLoading.visibility = View.GONE
        binding.clEmptyList.visibility = View.GONE
        binding.clNoInternet.visibility = View.GONE
    }

    private fun highlightFilterButton(isHighlighted: Boolean) {
        binding.filterButton.setImageResource(
            if (isHighlighted) R.drawable.ic_filter_on else R.drawable.ic_filter
        )
    }

    private fun hasActiveFilters(): Boolean {
        val filterSettings = filterInteractor.loadFilterSettings()
        return !filterSettings.location.isNullOrBlank() ||
            !filterSettings.salary.isNullOrBlank() ||
            !filterSettings.industry.isNullOrBlank() ||
            !filterSettings.area.isNullOrBlank() ||
            filterSettings.hideWithoutSalary
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val KEY_VACANCY = "vacancyId"
    }

    private fun openFilterFragment() {
        findNavController().navigate(R.id.action_searchFragment_to_filterFragment)
    }

    private fun observePaginationLoading() {
        viewModel.isNextPageLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.nextPageLoading.visibility = View.VISIBLE
            } else {
                binding.nextPageLoading.visibility = View.GONE
            }
        }
    }

    private fun setupRecyclerView() {
        searchItemAdapter = SearchItemAdapter { vacancyId ->
            val bundle = Bundle().apply {
                putString(KEY_VACANCY, vacancyId)
            }
            findNavController().navigate(R.id.action_searchFragment_to_vacanciesFragment, bundle)
        }

        binding.vacancyList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchItemAdapter
        }

        binding.vacancyList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    }

    private fun handleError() {
        viewModel.errorEvent.observe(viewLifecycleOwner) { error ->
            when (error) {
                "no_internet" -> {
                    binding.pbLoading.visibility = View.GONE
                    Toast.makeText(requireContext(), getString(R.string.no_internet_toast), Toast.LENGTH_SHORT).show()
                }
                "server_error" -> {
                    binding.pbLoading.visibility = View.GONE
                    Toast.makeText(requireContext(), getString(R.string.error_toast), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
