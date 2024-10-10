package ru.practicum.android.diploma.favorites.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFavoritesBinding
import ru.practicum.android.diploma.favorites.presentation.FavoritesViewModel
import ru.practicum.android.diploma.search.presentation.models.UiScreenState
import ru.practicum.android.diploma.search.presentation.models.VacancyUi
import ru.practicum.android.diploma.search.ui.SearchFragment.Companion.KEY_VACANCY

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<FavoritesViewModel>()
    private var favoriteItemAdapter: FavoriteItemAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiState.observe(viewLifecycleOwner) {
            renderUiState(it)
        }

        favoriteItemAdapter = FavoriteItemAdapter { vacancyId ->
            val bundle = Bundle().apply {
                putString(KEY_VACANCY, vacancyId)
            }
            findNavController().navigate(R.id.action_favoritesFragment_to_vacanciesFragment, bundle)
        }

        binding.vacancyList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoriteItemAdapter
        }
    }

    private fun renderUiState(state: UiScreenState) {
        when (state) {
            UiScreenState.Empty -> showEmptyState()
            is UiScreenState.Success -> showSuccessState(state.vacancies, state.found)
            else -> {
                showErrorState()
            }
        }
    }

    private fun showErrorState() {
        hideAll()
        binding.errorState.visibility = View.VISIBLE
    }

    private fun showSuccessState(vacancies: List<VacancyUi>, found: Any) {
        hideAll()
        favoriteItemAdapter?.update(vacancies)
        binding.vacancyList.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        hideAll()
        binding.emptyState.visibility = View.VISIBLE
    }

    private fun hideAll() {
        binding.errorState.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
        binding.vacancyList.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
