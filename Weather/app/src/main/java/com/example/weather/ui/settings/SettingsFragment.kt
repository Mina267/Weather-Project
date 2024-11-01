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
                getString(R.string.language_english) -> "en"  // Use localized string
                getString(R.string.language_arabic) -> "ar"   // Use localized string
                else -> "en"
            }
            if (languageCode != settingsViewModel.language.value) {
                settingsViewModel.setLanguage(languageCode)
                changeLocale(languageCode)

                showToast(getString(R.string.language_set_to, selectedLanguage))  // Use localized string
            }
        }

        setupSpinner(binding.spinnerTempUnit, R.array.temp_units) { selectedUnit ->
            val tempUnit = when (selectedUnit) {
                getString(R.string.unit_celsius) -> "metric"
                getString(R.string.unit_fahrenheit) -> "imperial"
                getString(R.string.unit_kelvin) -> "standard"
                else -> "metric"
            }
            if (tempUnit != settingsViewModel.unit.value) {
                settingsViewModel.setUnit(tempUnit)
                showToast(getString(R.string.temperature_unit_set_to, selectedUnit))  // Use localized string
            }
        }

        setupSpinner(binding.spinnerWindSpeedUnit, R.array.wind_speed_units) { selectedUnit ->
            val windSpeedUnit = when (selectedUnit) {
                getString(R.string.unit_meter_per_sec) -> "metric"
                getString(R.string.unit_miles_per_hour) -> "imperial"
                else -> "metric"
            }
            if (windSpeedUnit != settingsViewModel.windSpeedUnit.value) {
                settingsViewModel.setWindSpeedUnit(windSpeedUnit)
                showToast(getString(R.string.wind_speed_unit_set_to, selectedUnit))  // Use localized string
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
            val languageLabel = when (language) {
                "en" -> getString(R.string.language_english) // Localized string
                "ar" -> getString(R.string.language_arabic)  // Localized string
                else -> getString(R.string.language_english) // Default to English
            }
            updateSpinnerSelection(binding.spinnerLanguage, languageLabel)
        }

        settingsViewModel.unit.observe(viewLifecycleOwner) { unit ->
            val tempUnitLabel = when (unit) {
                "metric" -> getString(R.string.unit_celsius)      // Localized string
                "imperial" -> getString(R.string.unit_fahrenheit) // Localized string
                "standard" -> getString(R.string.unit_kelvin)     // Localized string
                else -> getString(R.string.unit_celsius)          // Default to Celsius
            }
            updateSpinnerSelection(binding.spinnerTempUnit, tempUnitLabel)
        }

        settingsViewModel.windSpeedUnit.observe(viewLifecycleOwner) { windSpeed ->
            val windSpeedUnitLabel = when (windSpeed) {
                "metric" -> getString(R.string.unit_meter_per_sec)   // Localized string
                "imperial" -> getString(R.string.unit_miles_per_hour) // Localized string
                else -> getString(R.string.unit_meter_per_sec)        // Default to Meter/sec
            }
            updateSpinnerSelection(binding.spinnerWindSpeedUnit, windSpeedUnitLabel)
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
