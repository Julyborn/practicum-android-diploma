package ru.practicum.android.diploma.vacancies.ui

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacanciesBinding
import ru.practicum.android.diploma.util.formatSalary
import ru.practicum.android.diploma.vacancies.domain.models.VacancyDetails
import ru.practicum.android.diploma.vacancies.presentation.VacanciesViewModel
import ru.practicum.android.diploma.vacancies.presentation.VacancyScreenState

class VacanciesFragment : Fragment() {

    private var _binding: FragmentVacanciesBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<VacanciesViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVacanciesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vacancyId = arguments?.getString(KEY_VACANCY).toString()
        vacancyId.let {
            viewModel.loadVacancyDetails(it)
        }

        viewModel.screenState.observe(viewLifecycleOwner) { screenState -> handleScreenState(screenState) }
        binding.shareButton.setOnClickListener { shareVacancy(vacancyId) }
        binding.backButton.setOnClickListener { findNavController().popBackStack() }

        binding.addToFavoriteButton.setOnClickListener {
            viewModel.addToFavoriteHandler()
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            isFavorite?.let {
                if (it) {
                    binding.addToFavoriteButton.setImageResource(R.drawable.ic_button_remove_from_favorite)
                } else {
                    binding.addToFavoriteButton.setImageResource(R.drawable.ic_button_add_to_favorite)
                }
            }
        }
    }

    private fun displayVacancyDetails(vacancyDetails: VacancyDetails) {
        binding.employerArea.text = vacancyDetails.address.takeIf { !it.isNullOrEmpty() } ?: vacancyDetails.areaName
        binding.skills.visibility = if (vacancyDetails.skills.isNullOrEmpty()) View.GONE else View.VISIBLE
        vacancyDetails.skills?.let {
            binding.skillsValue.text = Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY)
            loadEmployerLogo(vacancyDetails.employer?.logoUrl)
        }
        binding.vacancyName.text = vacancyDetails.name
        binding.salaryValue.text = vacancyDetails.salary.formatSalary()
        binding.employerName.text = vacancyDetails.employer?.name
        binding.experienceValue.text = vacancyDetails.experience
        binding.employmentValue.text = vacancyDetails.employment
        binding.descriptionValue.text =
            Html.fromHtml(vacancyDetails.description.trimIndent(), Html.FROM_HTML_MODE_LEGACY)

    }

    private fun handleScreenState(screenState: VacancyScreenState) {
        when (screenState) {
            is VacancyScreenState.Loading -> showLoadingState()
            is VacancyScreenState.Success -> showSuccessState(screenState.vacancyDetails)
            is VacancyScreenState.Error -> {
                when (screenState.errorType) {
                    VacancyScreenState.ErrorType.NOT_FOUND -> showErrorState()
                    VacancyScreenState.ErrorType.SERVER_ERROR -> showErrorState()
                    VacancyScreenState.ErrorType.NO_INTERNET -> showNoInternetState()
                }
            }
        }
    }

    private fun showLoadingState() {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.noVacancyPlaceholder.visibility = View.GONE
        binding.clVacancyDetails.visibility = View.GONE
        binding.vacancyServerError.visibility = View.GONE
    }

    private fun showSuccessState(vacancyDetails: VacancyDetails) {
        binding.clVacancyDetails.visibility = View.VISIBLE
        binding.loadingIndicator.visibility = View.GONE
        binding.noVacancyPlaceholder.visibility = View.GONE
        binding.vacancyServerError.visibility = View.GONE
        displayVacancyDetails(vacancyDetails)
    }

    private fun showErrorState() {
        binding.loadingIndicator.visibility = View.GONE
        binding.clVacancyDetails.visibility = View.GONE
        binding.noVacancyPlaceholder.visibility = View.VISIBLE
    }

    private fun showNoInternetState() {
        binding.loadingIndicator.visibility = View.GONE
        binding.clVacancyDetails.visibility = View.GONE
        binding.vacancyServerNoInternetError.visibility = View.VISIBLE
    }

    private fun loadEmployerLogo(logoUrl: String?) {
        Glide.with(this)
            .load(logoUrl)
            .placeholder(R.drawable.ic_placeholder_logo)
            .error(R.drawable.ic_placeholder_logo)
            .centerCrop()
            .transform(
                CenterCrop(),
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        RADIUS,
                        resources.displayMetrics
                    ).toInt()
                )
            )
            .into(binding.employerLogo)
    }

    private fun shareVacancy(vacancyId: String) {
        val shareMessage = getString(R.string.share_vacancy_message, vacancyId)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_vacancy_title)))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val RADIUS = 12F
        const val KEY_VACANCY = "vacancyId"
    }
}
