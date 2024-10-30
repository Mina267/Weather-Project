package com.example.weather.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.weather.R
import com.example.weather.sharedpreference.SharedPreferenceDataSourceImpl
import com.example.weather.databinding.FragmentSettingsBinding
import com.example.weather.db.WeatherDataBase
import com.example.weather.db.WeatherLocalDataSourceImpl
import com.example.weather.model.WeatherRepositoryImpl
import com.example.weather.network.ApiService
import com.example.weather.network.NetworkConnectionStatusImpl
import com.example.weather.network.RetrofitHelper
import com.example.weather.network.WeatherRemoteDataSourceImpl
import java.util.Locale

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(
            WeatherRepositoryImpl(
                WeatherRemoteDataSourceImpl(RetrofitHelper.getInstance().create(ApiService::class.java)),
                WeatherLocalDataSourceImpl(WeatherDataBase.getInstance(requireContext()).weatherDao()),
                SharedPreferenceDataSourceImpl.getInstance(requireContext()),
                NetworkConnectionStatusImpl.getInstance(requireContext())
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        setupSpinners()
        observeViewModel()
        setupSwitchListener()

        return binding.root
    }

    private fun setupSpinners() {
        setupSpinner(binding.spinnerLanguage, R.array.language_options) { selectedLanguage ->
            val languageCode = when (selectedLanguage) {
                "English" -> "en"
                "Arabic" -> "ar"
                else -> "en"
            }
            if (languageCode != settingsViewModel.language.value) {
                settingsViewModel.setLanguage(languageCode)
                changeLocale(languageCode)

                showToast("Language set to $selectedLanguage")
            }
        }

        setupSpinner(binding.spinnerTempUnit, R.array.temp_units) { selectedUnit ->
            val tempUnit = when (selectedUnit) {
                "Celsius" -> "metric"
                "Fahrenheit" -> "imperial"
                "Kelvin" -> "standard"
                else -> "metric"
            }
            if (tempUnit != settingsViewModel.unit.value) {
                settingsViewModel.setUnit(tempUnit)
                showToast("Temperature unit set to $selectedUnit")
            }
        }

        setupSpinner(binding.spinnerWindSpeedUnit, R.array.wind_speed_units) { selectedUnit ->
            val windSpeedUnit = when (selectedUnit) {
                 "Meter/sec" -> "metric"
                 "Miles/hour" -> "imperial"
                else -> "metric" // Default
            }
            if (windSpeedUnit != settingsViewModel.windSpeedUnit.value) {
                settingsViewModel.setWindSpeedUnit(windSpeedUnit)
                showToast("Wind speed unit set to $selectedUnit")
            }
        }
    }

    private fun setupSpinner(spinner: Spinner, arrayResId: Int, onSelect: (String) -> Unit) {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            arrayResId,
            R.layout.spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedValue = parent.getItemAtPosition(position).toString()
                onSelect(selectedValue)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


    private fun observeViewModel() {
        settingsViewModel.language.observe(viewLifecycleOwner) { language ->
            updateSpinnerSelection(binding.spinnerLanguage, when (language) {
                "en" -> "English"
                "ar" -> "Arabic"
                else -> "English"
            })
        }

        settingsViewModel.unit.observe(viewLifecycleOwner) { unit ->
            updateSpinnerSelection(binding.spinnerTempUnit, when (unit) {
                "metric" -> "Celsius"
                "imperial" -> "Fahrenheit"
                "standard" -> "Kelvin"
                else -> "Celsius"
            })
        }

        settingsViewModel.windSpeedUnit.observe(viewLifecycleOwner) { windSpeed ->
            updateSpinnerSelection(binding.spinnerWindSpeedUnit, when (windSpeed) {
                "metric" -> "Meter/sec"
                "imperial" -> "Miles/hour"
                else -> "Metric"
            })
        }
    }

    private fun updateSpinnerSelection(spinner: Spinner, value: String) {
        val position = (spinner.adapter as ArrayAdapter<String>).getPosition(value)
        if (spinner.selectedItemPosition != position) {
            spinner.setSelection(position)
        }
    }

    private fun setupSwitchListener() {
        binding.switchLocation.apply {
            isChecked = settingsViewModel.locationSource.value ?: false
            setOnCheckedChangeListener { _, isChecked ->
                settingsViewModel.setLocationSource(isChecked)
                showToast("Location source set to ${if (isChecked) "GPS" else "Map"}")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    private fun changeLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = requireContext().resources.configuration
        config.setLocale(locale)
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)

        requireActivity().recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
