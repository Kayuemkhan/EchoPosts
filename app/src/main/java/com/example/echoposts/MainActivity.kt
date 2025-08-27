package com.example.echoposts
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.echoposts.data.ThemeManager
import com.example.echoposts.data.repository.AuthRepository
import com.example.echoposts.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply theme after injection is complete
        themeManager.applyTheme()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Check if user is logged in
        if (!authRepository.isLoggedIn()) {
            // Navigate to login screen
            navController.navigate(R.id.loginFragment)
        }

        navView.setupWithNavController(navController)

        // Observe theme changes
        observeThemeChanges()
    }

    private fun observeThemeChanges() {
        lifecycleScope.launch {
            themeManager.isDarkMode.collect { isDarkMode ->
                // Handle theme changes if needed
                // Note: Activity recreation is handled automatically by AppCompatDelegate
            }
        }
    }
}
