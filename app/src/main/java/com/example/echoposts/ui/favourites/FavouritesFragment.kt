package com.example.echoposts.ui.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.echoposts.R
import com.example.echoposts.databinding.FragmentFavouritesBinding
import com.example.echoposts.domain.model.Post
import com.example.echoposts.domain.model.UiState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavouritesFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavouritesViewModel by viewModels()
    private lateinit var favouritesAdapter: FavouritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        setupMenu()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        favouritesAdapter = FavouritesAdapter(
            onRemoveClick = { post ->
                showRemoveConfirmationDialog(post)
            },
            onPostClick = { post ->
                // Handle post click - maybe navigate to detail screen
                // findNavController().navigate(FavouritesFragmentDirections.actionToPostDetail(post.id))
            }
        )

        binding.recyclerView.apply {
            adapter = favouritesAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            )
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshFavourites()
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.favourites_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_clear_all -> {
                        showClearAllConfirmationDialog()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun observeViewModel() {
        // Observe favourite posts state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favouritePostsState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyStateLayout.visibility = View.GONE
                        binding.errorLayout.visibility = View.GONE
                    }
                    is UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.emptyStateLayout.visibility = View.GONE
                        binding.errorLayout.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        favouritesAdapter.submitList(state.data)
                    }
                    is UiState.Empty -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerView.visibility = View.GONE
                        binding.errorLayout.visibility = View.GONE
                        binding.emptyStateLayout.visibility = View.VISIBLE
                    }
                    is UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyStateLayout.visibility = View.GONE
                        binding.errorLayout.visibility = View.VISIBLE
                        binding.tvError.text = state.message
                    }
                }
            }
        }

        // Observe refresh state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isRefreshing.collect { isRefreshing ->
                binding.swipeRefreshLayout.isRefreshing = isRefreshing
            }
        }

        // Observe deleting posts
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deletingPostIds.collect { deletingIds ->
                favouritesAdapter.setDeletingPosts(deletingIds)
            }
        }
    }

    private fun showRemoveConfirmationDialog(post: Post) {
        AlertDialog.Builder(requireContext())
            .setTitle("Remove from Favourites")
            .setMessage("Are you sure you want to remove \"${post.title}\" from your favourites?")
            .setPositiveButton("Remove") { _, _ ->
                viewModel.removeFromFavourites(post)
                showSnackbar("Removed from favourites")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showClearAllConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Clear All Favourites")
            .setMessage("Are you sure you want to remove all posts from your favourites? This action cannot be undone.")
            .setPositiveButton("Clear All") { _, _ ->
                viewModel.clearAllFavourites()
                showSnackbar("All favourites cleared")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}