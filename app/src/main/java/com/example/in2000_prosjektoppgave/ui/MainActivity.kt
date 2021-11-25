package com.example.in2000_prosjektoppgave.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.in2000_prosjektoppgave.R
import com.example.in2000_prosjektoppgave.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import org.conscrypt.Conscrypt
import java.security.Security

/**
 * Main activity: Host for all fragments. Only activity in app.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // Binding for views
    private lateinit var binding: ActivityMainBinding

    // Navigation controller
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Update security suit for network calls
        Security.insertProviderAt(Conscrypt.newProvider(), 1)

        // Set up navigation
        setUpNavigation()
    }


    override fun onSupportNavigateUp(): Boolean {
        // Navigate up for child fragments
        return navController.navigateUp()
    }


    /**
     * Function to set up Navigation for the app.
     */
    private fun setUpNavigation() {

        // Fragments without bottom navigation bar
        val destinations = listOf(R.id.driftyDetailFragment, R.id.mapViewFragment, R.id.mapsSearchFragment)

        // Host fragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // Bottom App bar set up
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.driftyFragment, R.id.weatherFragment))

        // Set up of navigation
        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Hide bottom navigation bar for some destinations
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in destinations) {
                binding.bottomNavigation.visibility = View.GONE
            } else {
                binding.bottomNavigation.visibility = View.VISIBLE
            }
        }
    }

}