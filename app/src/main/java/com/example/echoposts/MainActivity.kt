package com.example.echoposts
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.echoposts.data.ThemeManager
import com.example.echoposts.data.repository.AuthRepository
import com.example.echoposts.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var navView : BottomNavigationView

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeManager.applyTheme()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        if (!authRepository.isLoggedIn()) {
            navController.navigate(R.id.loginFragment)
        }

        navView.setupWithNavController(navController)

        observeThemeChanges()


        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }


        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_home ||
                destination.id == R.id.navigation_search ||
                destination.id == R.id.navigation_favourites
            ) {
                showBottomNav()
            } else {
                hideBottomNav()
            }
        }

        navView.setupWithNavController(navController)



    }
    private fun hideBottomNav() {
        navView.visibility = View.GONE
    }

    private fun showBottomNav() {
        navView.visibility = View.VISIBLE
    }

    private fun observeThemeChanges() {
        lifecycleScope.launch {
            themeManager.isDarkMode.collect {

            }
        }
    }
}
