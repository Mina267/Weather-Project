package com.example.weather.ui.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.sharedpreference.SharedPreferenceDataSourceImpl
import com.example.weather.databinding.FragmentFavouriteBinding
import com.example.weather.db.WeatherDataBase
import com.example.weather.db.WeatherLocalDataSourceImpl
import com.example.weather.model.ShowSnackbar
import com.example.weather.model.WeatherRepositoryImpl
import com.example.weather.network.ApiService
import com.example.weather.network.NetworkConnectionStatus
import com.example.weather.network.NetworkConnectionStatusImpl
import com.example.weather.network.RetrofitHelper
import com.example.weather.network.WeatherRemoteDataSourceImpl
import kotlinx.coroutines.launch

class FavouriteFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    private val favouriteViewModel: FavouriteViewModel by viewModels {
        FavouriteViewModelFactory(
            WeatherRepositoryImpl(
                WeatherRemoteDataSourceImpl(RetrofitHelper.getInstance().create(ApiService::class.java)),
                WeatherLocalDataSourceImpl(WeatherDataBase.getInstance(requireContext()).weatherDao()),
                SharedPreferenceDataSourceImpl.getInstance(requireContext()),
                NetworkConnectionStatusImpl.getInstance(requireContext())

            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize the RecyclerView adapter
        binding.favRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ListAdapterFav(
                myListener = { favourite ->
                    favouriteViewModel.setLocation(favourite.lat, favourite.lon)
                    findNavController().navigate(R.id.action_nav_favorite_to_nav_home)
                },
                removeListener = { favourite ->
                    favouriteViewModel.deleteFavourite(favourite.lat, favourite.lon)
                    ShowSnackbar.customSnackbar(
                        context = requireContext(),
                        view = requireView(),
                        message = " " + getString(R.string.fav_delete_successfully),
                        actionText = "Undo",
                        iconResId = R.drawable.delete_24,
                        action = { view ->
                            favouriteViewModel.insertFavourite(favourite)
                        }
                    )
                }
            )
        }

        binding.floatingFavButton.setOnClickListener {
            val navController = findNavController()
            if (navController.currentDestination?.id == R.id.nav_favorite) {
                navController.navigate(R.id.action_nav_favorite_to_searchFragment)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            favouriteViewModel.weatherFavourites.collect { favourites ->
                _binding?.let { binding ->
                    (binding.favRecyclerView.adapter as ListAdapterFav).submitList(favourites)

                    if (favourites.isEmpty()) {
                        binding.imgViewNoFav.visibility = View.VISIBLE
                        binding.favRecyclerView.visibility = View.GONE
                    } else {
                        binding.imgViewNoFav.visibility = View.GONE
                        binding.favRecyclerView.visibility = View.VISIBLE
                    }
                }
            }
        }


        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

