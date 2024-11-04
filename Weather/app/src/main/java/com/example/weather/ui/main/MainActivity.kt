package com.example.weather.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.sharedpreference.SharedPreferenceDataSourceImpl
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.db.WeatherDataBase
import com.example.weather.db.WeatherLocalDataSourceImpl
import com.example.weather.model.WeatherRepositoryImpl
import com.example.weather.network.ApiService
import com.example.weather.network.NetworkConnectionStatusImpl
import com.example.weather.network.RetrofitHelper
import com.example.weather.network.WeatherRemoteDataSourceImpl
import com.example.weather.ui.favourite.FavouriteViewModel
import com.example.weather.ui.favourite.FavouriteViewModelFactory
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: FavouriteViewModel by viewModels {
        FavouriteViewModelFactory(
            WeatherRepositoryImpl(
                WeatherRemoteDataSourceImpl(RetrofitHelper.getInstance().create(ApiService::class.java)),
                WeatherLocalDataSourceImpl(WeatherDataBase.getInstance(this).weatherDao()),
                SharedPreferenceDataSourceImpl.getInstance(this),
                NetworkConnectionStatusImpl.getInstance(this)
            )
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)



        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home/*, R.id.nav_favorite*/, R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->


            if (destination.id == R.id.nav_settings) {
                binding.appBarMain.toolbar.visibility = View.GONE
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            } else {
                binding.appBarMain.toolbar.visibility = View.VISIBLE
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }


            // Dynamically update the toolbar title
            binding.appBarMain.toolbar.title = when (destination.id) {
                R.id.nav_home -> getString(R.string.menu_home)
                R.id.nav_favorite -> getString(R.string.menu_fav)
                R.id.nav_settings -> getString(R.string.menu_setting)
                R.id.searchFragment -> getString(R.string.menu_search)
                R.id.alertFragment -> getString(R.string.menu_alert)
                else -> getString(R.string.app_name) // default label
            }
        }



        val preferredLanguage = SharedPreferenceDataSourceImpl.getInstance(this).getLanguage()
        if (preferredLanguage == "ar") {
            changeLocale("ar")
        }

        updateSettingsTitle(navView)

        // Setup RecyclerView in Navigation Drawer
        setupNavigationRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //menuInflater.inflate(R.menu.main, menu)
        return true
    }

    private fun updateSettingsTitle(navView: NavigationView) {
        val menu = navView.menu
        val settingsMenuItem = menu.findItem(R.id.nav_settings)
        settingsMenuItem.title = getString(R.string.menu_setting)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    private fun changeLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = this.resources.configuration

        config.setLocale(locale)
        this.resources.updateConfiguration(config, this.resources.displayMetrics)
        //this.recreate()

    }


    private fun setupNavigationRecyclerView() {

        val headerView = binding.navView.getHeaderView(0)
        val navRecyclerView = headerView.findViewById<RecyclerView>(R.id.navRecyclerView)
        val imgSearch = headerView.findViewById<ImageView>(R.id.imgSearch)
        val txtMyCurrentLocation = headerView.findViewById<TextView>(R.id.txtMyCurrentLocation)
        val txtManageLocations = headerView.findViewById<TextView>(R.id.txtManageLocations)

        txtManageLocations.text = getString(R.string.managelocations)
        txtMyCurrentLocation.text = getString(R.string.mycurrentlocation)


        navRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ListAdapterMain(
                myListener = { favourite ->
                    mainViewModel.setLocation(favourite.lat, favourite.lon)

                    val navController = findNavController(R.id.nav_host_fragment_content_main)
                    navController.navigate(R.id.nav_home, null,
                        androidx.navigation.NavOptions.Builder()
                            .setPopUpTo(R.id.nav_home, inclusive = true)
                            .setLaunchSingleTop(true)
                            .build()
                    )

                    mainViewModel.setLocationSource(false)

                    navRecyclerView.postDelayed({
                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                    }, 200)
                },
                removeListener = { favourite ->
                    mainViewModel.deleteFavourite(favourite.lat, favourite.lon)
                }
            )
        }


        txtMyCurrentLocation.setOnClickListener {
            mainViewModel.setLocationSource(true)
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.nav_home)
            navRecyclerView.postDelayed({
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }, 100)
        }


        lifecycleScope.launchWhenResumed {
            mainViewModel.weatherFavourites.collect { favourites ->
                (navRecyclerView.adapter as ListAdapterMain).submitList(favourites)
            }
        }

        txtManageLocations.setOnClickListener {
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.nav_favorite)
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }




        imgSearch.setOnClickListener {
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.searchFragment)
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }


}
