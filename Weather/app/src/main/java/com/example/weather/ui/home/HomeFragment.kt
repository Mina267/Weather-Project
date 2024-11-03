package com.example.weather.ui.home

import android.os.Bundle
import kotlin.math.roundToInt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.model.OneCallWeather
import com.example.weather.model.WeatherRepositoryImpl
import com.example.weather.network.ApiService
import com.example.weather.network.RetrofitHelper
import com.example.weather.network.WeatherRemoteDataSourceImpl
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.sharedpreference.SharedPreferenceDataSourceImpl
import com.example.weather.db.WeatherDataBase
import com.example.weather.db.WeatherLocalDataSourceImpl
import com.example.weather.model.LocationHandler
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.weather.model.ApiHomeState
import com.example.weather.network.NetworkConnectionStatusImpl
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationHandler: LocationHandler
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var  hourlyAdapter: ListAdapterHours
    private lateinit var dailyAdapter: ListAdapterDaily
    private lateinit var listAdapterInfo: ListAdapterInfo

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val repository = WeatherRepositoryImpl(
            WeatherRemoteDataSourceImpl(RetrofitHelper.getInstance().create(ApiService::class.java)),
            WeatherLocalDataSourceImpl(WeatherDataBase.getInstance(requireContext()).weatherDao()),
            SharedPreferenceDataSourceImpl.getInstance(requireContext()),
            NetworkConnectionStatusImpl.getInstance(requireContext())

        )
        val weatherViewModelFactory = WeatherViewModelFactory(repository)
        weatherViewModel = ViewModelProvider(this, weatherViewModelFactory).get(WeatherViewModel::class.java)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationHandler = LocationHandler(requireContext(), fusedLocationProviderClient)


        hourlyAdapter = ListAdapterHours {}


        binding.recyclerViewHourly.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewHourly.adapter = hourlyAdapter

        dailyAdapter = ListAdapterDaily { dailyItem ->
        }

        binding.recyclerViewDaily.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dailyAdapter
        }


        binding.floatingNotificationButton.setOnClickListener{
            findNavController().navigate(R.id.action_nav_home_to_alertFragment) // Use SafeArgs if possible

        }

        listAdapterInfo = ListAdapterInfo { currentData ->

        }

        binding.recyclerViewInfo.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewInfo.adapter = listAdapterInfo




        viewLifecycleOwner.lifecycleScope.launch {
            weatherViewModel.weatherData.collect { state ->
                when (state) {
                    is ApiHomeState.Loading -> {
                        binding.progressBarHome.visibility = View.VISIBLE
                        binding.nestedScrollHome.visibility = View.GONE
                        binding.imgViewNoConnectionHome.visibility = View.GONE

                    }
                    is ApiHomeState.Success -> {
                        binding.progressBarHome.visibility = View.GONE
                        binding.nestedScrollHome.visibility = View.VISIBLE
                        binding.imgViewNoConnectionHome.visibility = View.GONE

                        updateUI(state.data)
                    }
                    is ApiHomeState.Failure -> {
                        binding.nestedScrollHome.visibility = View.GONE
                        binding.progressBarHome.visibility = View.GONE
                        binding.imgViewNoConnectionHome.visibility = View.VISIBLE
                    }

                    null -> {

                        binding.nestedScrollHome.visibility = View.GONE
                        binding.progressBarHome.visibility = View.GONE
                        binding.imgViewNoConnectionHome.visibility = View.VISIBLE
                    }
                }
            }
        }





        return root
    }

    override fun onStart() {
        super.onStart()
        if (locationHandler.checkPermissions()) {
            if (locationHandler.isLocationEnabled()) {
                if (weatherViewModel.getLocationSource()) {
                    locationHandler.getSingleAccurateLocation { lat, lon ->
                        weatherViewModel.refreshWeather(lat, lon)
                        weatherViewModel.setActiveNetworkLocation(lat, lon)
                        Log.i("WeatherCheck", "onStart: lat" + lat)
                    }
                }
                else
                {

                    weatherViewModel.refreshWeather()

                }




            } else {
                locationHandler.enableLocationServices()

            }
        } else {
            locationHandler.requestPermissions(requireActivity() as AppCompatActivity)

        }

    }



    private fun updateUI(weather: OneCallWeather) {
        val currentWeather = weather.current
        val dailyWeather = weather.daily.firstOrNull()

        if (currentWeather != null) {
            val currentTemp = currentWeather.temp.roundToInt()
            val feelsLikeTemp = currentWeather.feelsLike.roundToInt()

            val maxTemp = dailyWeather?.temp?.max?.roundToInt() ?: 0
            val minTemp = dailyWeather?.temp?.min?.roundToInt() ?: 0

            // Log temperature details
            Log.d("WeatherUpdate", "Current Temperature: $currentTemp")
            Log.d("WeatherUpdate", "Feels Like: $feelsLikeTemp")
            Log.d("WeatherUpdate", "Max Temperature: $maxTemp")
            Log.d("WeatherUpdate", "Min Temperature: $minTemp")
            Log.d("WeatherUpdate", "Humidity: ${currentWeather.humidity}")
            Log.d("WeatherUpdate", "UV Index: ${currentWeather.uvi}")
            Log.d("WeatherUpdate", "Wind Speed: ${currentWeather.windSpeed}")
            Log.d("WeatherUpdate", "Description: ${currentWeather.weather.firstOrNull()?.description}")

            // Update the UI with rounded values
            binding.txtCurrentDegree.text = "${
                NumberFormat.getInstance(Locale.getDefault()).format(currentTemp)}°"
            binding.txtCurrentFeelsLike.text = "${NumberFormat.getInstance(Locale.getDefault()).format(maxTemp)}° / ${NumberFormat.getInstance(Locale.getDefault()).format(minTemp)}° ${getString(R.string.feels_like)}: ${NumberFormat.getInstance(Locale.getDefault()).format(feelsLikeTemp)}°"
            binding.txtCurrentWeatherDisc.text = currentWeather.weather.firstOrNull()?.description

            if (weatherViewModel.getLanguage() == "ar")
            {
                binding.txtHourlyDesc.text = currentWeather.weather.firstOrNull()?.description + ".   ${context?.getString(R.string.Low)}" +" ${NumberFormat.getInstance(Locale.getDefault()).format(minTemp)}°."

            } else {
                binding.txtHourlyDesc.text = weather.daily[0].summary + ". ${context?.getString(R.string.Low)}" +" ${NumberFormat.getInstance(Locale.getDefault()).format(minTemp)}°C."

            }

            hourlyAdapter.submitList(weather.hourly.drop(1).take(24))
            dailyAdapter.submitList(weather.daily.drop(1))

            val weatherInfoList = weatherViewModel.mapCurrentToWeatherInfo(weather.current, weather.wind)
            listAdapterInfo.submitList(weatherInfoList)
            val sunriseInMillis = currentWeather.sunrise.toLong() * 1000
            val sunsetInMillis = currentWeather.sunset.toLong() * 1000
            val currentTimeInMillis = System.currentTimeMillis()

            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            val sunriseFormatted = timeFormat.format(Date(sunriseInMillis))
            val sunsetFormatted = timeFormat.format(Date(sunsetInMillis))

            binding.txtSunrise.text = getString(R.string.sunrise)
            binding.txtSunset.text = getString(R.string.sunset)


            binding.txtSunRiseTime.text = sunriseFormatted
            binding.txtSunSetTime.text = sunsetFormatted

            binding.sunArcView.setSunTimes(sunriseInMillis, sunsetInMillis)
            binding.sunArcView.updateSunPosition(currentTimeInMillis)

            val address = locationHandler.getAddress(weather.lat, weather.lon)
            (activity as? AppCompatActivity)?.supportActionBar?.title = address



        } else {
            Log.d("WeatherUpdate", "No current weather data available.")
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }








    private fun requestNewLocation() {
        locationHandler.getSingleAccurateLocation { lat, lon ->
            weatherViewModel.refreshWeather(lat, lon)
            val address = locationHandler.getAddress(lat, lon)
            (activity as? AppCompatActivity)?.supportActionBar?.title = address

            Log.i("LocationUpdate", "New location retrieved: lat=$lat, lon=$lon")
        }
    }


    override fun onDestroy() {
        weatherViewModel.clear()
        super.onDestroy()
    }



}
