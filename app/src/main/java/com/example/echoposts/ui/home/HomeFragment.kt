package com.example.echoposts.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.echoposts.R
import com.example.echoposts.databinding.FragmentHomeBinding
import com.example.echoposts.domain.model.SearchState
import com.example.echoposts.ui.common.PaginationScrollListener
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        setupSearchView()
        setupRetryButton()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        postsAdapter = PostsAdapter(
            onFavouriteClick = { post ->
                viewModel.toggleFavourite(post)
            },
            onPostClick = { post ->
                // Handle post click - maybe navigate to detail screen
            }
        )

        layoutManager = LinearLayoutManager(context)

        binding.recyclerView.apply {
            adapter = postsAdapter
            layoutManager = this@HomeFragment.layoutManager
            addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            )

            // Add pagination scroll listener
            addOnScrollListener(object : PaginationScrollListener(this@HomeFragment.layoutManager) {
                override fun loadMoreItems() {
                    viewModel.loadNextPage()
                }

                override fun isLoading(): Boolean {
                    return viewModel.paginationState.value.isLoadingMore
                }

                override fun hasMoreData(): Boolean {
                    return viewModel.paginationState.value.hasMoreData && !viewModel.isSearchMode.value
                }
            })
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshPosts()
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onSearchQueryChanged(newText ?: "")
                return true
            }
        })

        binding.searchView.setOnCloseListener {
            viewModel.exitSearchMode()
            false
        }
    }

    private fun setupRetryButton() {
        binding.btnRetry.setOnClickListener {
            viewModel.clearError()
            if (viewModel.isSearchMode.value) {
                viewModel.onSearchQueryChanged(viewModel.searchQuery.value)
            } else {
                viewModel.refreshPosts()
            }
        }
    }

    private fun observeViewModel() {
        // Observe posts
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.posts.collect { posts ->
                val paginationState = viewModel.paginationState.value
                val isSearchMode = viewModel.isSearchMode.value

                postsAdapter.submitPostsWithLoading(
                    posts = posts,
                    showLoading = !isSearchMode && paginationState.isLoadingMore && paginationState.hasMoreData
                )
            }
        }

        // Observe search state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchState.collect { searchState ->
                when (searchState) {
                    is SearchState.Idle -> {
                        binding.searchProgress.visibility = View.GONE
                        binding.searchEmptyState.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                    }
                    is SearchState.Searching -> {
                        binding.searchProgress.visibility = View.VISIBLE
                        binding.searchEmptyState.visibility = View.GONE
                        binding.errorLayout.visibility = View.GONE
                    }
                    is SearchState.Results -> {
                        binding.searchProgress.visibility = View.GONE
                        binding.searchEmptyState.visibility = View.GONE
                        binding.errorLayout.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                    }
                    is SearchState.Empty -> {
                        binding.searchProgress.visibility = View.GONE
                        binding.recyclerView.visibility = View.GONE
                        binding.errorLayout.visibility = View.GONE
                        binding.searchEmptyState.visibility = View.VISIBLE
                        binding.tvEmptyMessage.text = "No posts found for \"${searchState.query}\""
                    }
                    is SearchState.Error -> {
                        binding.searchProgress.visibility = View.GONE
                        binding.searchEmptyState.visibility = View.GONE
                        if (viewModel.posts.value.isEmpty()) {
                            binding.recyclerView.visibility = View.GONE
                            binding.errorLayout.visibility = View.VISIBLE
                            binding.tvError.text = searchState.message
                        } else {
                            showErrorSnackbar("Search failed: ${searchState.message}")
                        }
                    }
                }
            }
        }

        // Observe pagination state (existing code)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.paginationState.collect { state ->
                when {
                    state.isLoading && !viewModel.isSearchMode.value -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.errorLayout.visibility = View.GONE
                        binding.recyclerView.visibility = View.GONE
                        binding.searchEmptyState.visibility = View.GONE
                    }
                    state.error != null && viewModel.posts.value.isEmpty() && !viewModel.isSearchMode.value -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerView.visibility = View.GONE
                        binding.errorLayout.visibility = View.VISIBLE
                        binding.searchEmptyState.visibility = View.GONE
                        binding.tvError.text = state.error
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                        binding.errorLayout.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.searchEmptyState.visibility = View.GONE

                        // Show snackbar for load more errors
                        if (state.error != null && viewModel.posts.value.isNotEmpty() && !viewModel.isSearchMode.value) {
                            showErrorSnackbar("Failed to load more posts: ${state.error}") {
                                viewModel.loadNextPage()
                            }
                            viewModel.clearError()
                        }
                    }
                }

                // Update posts with current loading state
                val posts = viewModel.posts.value
                val isSearchMode = viewModel.isSearchMode.value
                if (posts.isNotEmpty()) {
                    postsAdapter.submitPostsWithLoading(
                        posts = posts,
                        showLoading = !isSearchMode && state.isLoadingMore && state.hasMoreData
                    )
                }
            }
        }

        // Observe refresh state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isRefreshing.collect { isRefreshing ->
                binding.swipeRefreshLayout.isRefreshing = isRefreshing
            }
        }
    }

    private fun showErrorSnackbar(message: String, actionText: String = "Retry", action: () -> Unit = {}) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction(actionText) { action() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}