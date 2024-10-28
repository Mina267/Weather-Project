package com.example.weather.ui.search

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.weather.databinding.FragmentSearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.weather.R
import com.example.weather.sharedpreference.SharedPreferenceDataSourceImpl
import com.example.weather.db.WeatherDataBase
import com.example.weather.db.WeatherLocalDataSourceImpl
import com.example.weather.model.Favourites
import com.example.weather.model.ShowSnackbar
import com.example.weather.model.WeatherRepositoryImpl
import com.example.weather.network.ApiService
import com.example.weather.network.RetrofitHelper
import com.example.weather.network.WeatherRemoteDataSourceImpl
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.MapEventsOverlay
import java.io.IOException
import java.util.Locale

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private lateinit var marker: Marker
    private var lat : Double = 30.0;
    private var long: Double = 31.0;

    private val searchViewModel : SearchViewModel by viewModels {
        SearchViewModelFactory(
            WeatherRepositoryImpl(
                WeatherRemoteDataSourceImpl(RetrofitHelper.getInstance().create(ApiService::class.java)),
                WeatherLocalDataSourceImpl(WeatherDataBase.getInstance(requireContext()).weatherDao()),
                SharedPreferenceDataSourceImpl.getInstance(requireContext())
            )
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root = binding.root

        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        mapView = binding.mapView
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(10.0)

        val defaultLocation = GeoPoint(30.0, 31.00)
        mapView.controller.setCenter(defaultLocation)

        marker = Marker(mapView)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)


        mapView.overlays.add(marker)

        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                setMarker(p)

                lat = p.latitude
                long = p.longitude

                return true
            }
            override fun longPressHelper(p: GeoPoint): Boolean = false
        })
        mapView.overlays.add(mapEventsOverlay)

        setupAutoCompleteSearch()

        binding.saveFavoriteButton.setOnClickListener {
            ShowSnackbar.customSnackbar(requireContext(), requireView(), "  " + getAddress(lat, long) + "added", "",
                R.drawable.check_circle_24px,
            ) {}

            // Insert favorite into the database
            searchViewModel.insertFavourite(Favourites(lat = lat, lon = long))

            // Get the NavController and check the previous destination
            val navController = findNavController()
            val previousFragmentId = navController.previousBackStackEntry?.destination?.id

            if (previousFragmentId != null) {
                when (previousFragmentId) {
                    R.id.nav_home -> {
                        searchViewModel.setLocation(lat, long)
                        searchViewModel.setLocationSource(false)

                    }
                    R.id.nav_favorite -> {


                    }

                }
            }

            // Navigate back to the previous fragment
            navController.popBackStack()
        }



        return root
    }

    private fun setupAutoCompleteSearch() {
        val searchBox: AutoCompleteTextView = binding.searchBox
        val retrofit = Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(NominatimApi::class.java)

        // Ensure the suggestions list is initialized
        val suggestions = mutableListOf<String>()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions)
        searchBox.setAdapter(adapter)

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(query: CharSequence?, start: Int, before: Int, count: Int) {
                if (!query.isNullOrEmpty() && query.length > 2) { // Fetch only for meaningful inputs
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            val results = api.search(query.toString())
                            if (results.isNotEmpty()) {
                                suggestions.clear()
                                suggestions.addAll(results.map { it.displayName })
                                adapter.notifyDataSetChanged()
                            } else {
                                Log.d("SearchFragment", "No suggestions found for query: $query")
                            }
                        } catch (e: Exception) {
                            Log.e("SearchFragment", "Error fetching suggestions: ${e.message}")
                        }
                    }
                } else {
                    suggestions.clear()
                    adapter.notifyDataSetChanged()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchBox.setOnItemClickListener { _, _, position, _ ->
            val selectedLocation = suggestions[position]
            searchLocation(api, selectedLocation)
        }
    }


    private fun searchLocation(api: NominatimApi, query: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val results = api.search(query)
                if (results.isNotEmpty()) {
                    val location = results.first()
                    val point = GeoPoint(location.lat, location.lon)
                    setMarker(point)
                    mapView.controller.setCenter(point)
                    Toast.makeText(requireContext(), "Location set: ${location.displayName}", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("SearchFragment", "No locations found for query: $query")
                }
            } catch (e: Exception) {
                Log.e("SearchFragment", "Error setting location: ${e.message}")
            }
        }
    }

    private fun setMarker(point: GeoPoint) {
        marker.position = point
        marker.title = "Location: ${point.latitude}, ${point.longitude}"
        marker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.custom_marker)
        mapView.invalidate()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface NominatimApi {
        @GET("search")
        suspend fun search(
            @Query("q") query: String,
            @Query("format") format: String = "json",
            @Query("addressdetails") addressdetails: Int = 1,
            @Query("limit") limit: Int = 5,
            @Query("accept-language") language: String = "en"
        ): List<SearchResult>
    }

    data class SearchResult(val displayName: String, val lat: Double, val lon: Double)


    fun getAddress(lat: Double, lon: Double): String {
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val currentAddress = StringBuilder("")
                if (!address.subAdminArea.isNullOrBlank()) currentAddress.append(address.subAdminArea)
                return currentAddress.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

}
