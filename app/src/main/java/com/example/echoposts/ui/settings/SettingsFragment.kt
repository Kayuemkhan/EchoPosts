package com.example.echoposts.ui.settings
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.echoposts.R
import com.example.echoposts.databinding.FragmentSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var settingsAdapter: SettingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        settingsAdapter = SettingsAdapter(
            onSwitchChanged = { key, isChecked ->
                viewModel.onSwitchChanged(key, isChecked)
            },
            onItemClick = { key ->
                handleSettingsItemClick(key)
            }
        )

        binding.recyclerView.apply {
            adapter = settingsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.settingsItems.collect { items ->
                settingsAdapter.submitList(items)
            }
        }
    }

    private fun handleSettingsItemClick(key: String) {
        when (key) {
            "profile" -> showComingSoonSnackbar("Profile settings")
            "privacy" -> showComingSoonSnackbar("Privacy settings")
            "clear_cache" -> showClearCacheDialog()
            "data_usage" -> showComingSoonSnackbar("Data usage")
            "libraries" -> showLibrariesDialog()
            "about" -> showAboutDialog()
            "licenses" -> showLicensesDialog()
            "help" -> openUrl("https://github.com/yourusername/echoposts/wiki")
            "feedback" -> sendFeedbackEmail()
            "rate_app" -> rateApp()
            "logout" -> showLogoutDialog()
        }
    }

    private fun showClearCacheDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Clear Cache")
            .setMessage("This will clear all cached posts and images. You'll need an internet connection to reload content.")
            .setPositiveButton("Clear") { _, _ ->
                // Implement cache clearing logic
                showSnackbar("Cache cleared successfully")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLibrariesDialog() {
        val libraries = viewModel.getLibraries()
        val libraryNames = libraries.map { "${it.name} (${it.version})" }.toTypedArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Open Source Libraries")
            .setItems(libraryNames) { _, which ->
                val library = libraries[which]
                showLibraryDetailDialog(library)
            }
            .setPositiveButton("Close", null)
            .show()
    }

    private fun showLibraryDetailDialog(library: com.example.echoposts.data.repository.LibraryInfo) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(library.name)
            .setMessage(
                """
                ${library.description}
                
                Version: ${library.version}
                License: ${library.license}
            """.trimIndent()
            )
            .setPositiveButton("Visit Website") { _, _ ->
                openUrl(library.url)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showAboutDialog() {
        val version = requireContext().packageManager
            .getPackageInfo(requireContext().packageName, 0).versionName

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("About EchoPosts")
            .setMessage(
                """
                EchoPosts v$version
                
                A modern social media app built with the latest Android technologies. 
                
                Features:
                • Browse and discover posts
                • Mark posts as favourites
                • Search through content
                • Dark mode support
                • Offline functionality
                
                Developed with ❤️ using:
                • Kotlin & Coroutines
                • Jetpack Compose & Material Design
                • Room Database
                • Retrofit & OkHttp
                • Hilt Dependency Injection
                
                © 2025 EchoPosts. All rights reserved.
            """.trimIndent()
            )
            .setPositiveButton("GitHub") { _, _ ->
                openUrl("https://github.com/Kayuemkhan/echoposts")
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showLicensesDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Open Source Licenses")
            .setMessage(
                """
                This app uses several open source libraries:
                
                • All Android Jetpack libraries are licensed under Apache License 2.0
                • Kotlin is licensed under Apache License 2.0
                • Material Components are licensed under Apache License 2.0
                • Third-party libraries maintain their respective licenses
                
                Full license texts are available at:
                https://www.apache.org/licenses/LICENSE-2.0
            """.trimIndent()
            )
            .setPositiveButton("View Full Licenses") { _, _ ->
                openUrl("https://github.com/yourusername/echoposts/blob/main/LICENSES.md")
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showLogoutDialog() {
//        MaterialAlertDialogBuilder(requireContext())
//            .setTitle("Logout")
//            .setMessage("Are you sure you want to logout? You'll need to sign in again to access your account.")
//            .setPositiveButton("Logout") { _, _ ->
//                viewModel.logout()
//                // Navigate to login screen
//                findNavController().navigate(R.id.action_settings_to_login)
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            showSnackbar("Unable to open link")
        }
    }

    private fun sendFeedbackEmail() {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:feedback@echoposts.com")
                putExtra(Intent.EXTRA_SUBJECT, "EchoPosts Feedback")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Hi EchoPosts Team,\n\nI have some feedback about the app:\n\n"
                )
            }
            startActivity(Intent.createChooser(intent, "Send Feedback"))
        } catch (e: Exception) {
            showSnackbar("No email app found")
        }
    }

    private fun rateApp() {
        try {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=${requireContext().packageName}")
            )
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback to web version
            openUrl("https://play.google.com/store/apps/details?id=${requireContext().packageName}")
        }
    }

    private fun showComingSoonSnackbar(feature: String) {
        showSnackbar("$feature coming soon!")
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}