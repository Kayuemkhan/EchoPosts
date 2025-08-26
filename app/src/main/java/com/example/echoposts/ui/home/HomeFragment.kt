package com.example.echoposts.ui.home



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.echoposts.databinding.FragmentHomeBinding
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
        setupRetryButton()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        postsAdapter = PostsAdapter(
            onFavouriteClick = { post ->
                viewModel.toggleFavourite(post)
            },
            onPostClick = {

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
                    return viewModel.paginationState.value.hasMoreData
                }
            })
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshPosts()
        }
    }

    private fun setupRetryButton() {
        binding.btnRetry.setOnClickListener {
            viewModel.clearError()
            viewModel.refreshPosts()
        }
    }

    private fun observeViewModel() {
        // Observe posts
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.posts.collect { posts ->
                val paginationState = viewModel.paginationState.value
                postsAdapter.submitPostsWithLoading(
                    posts = posts,
                    showLoading = paginationState.isLoadingMore && paginationState.hasMoreData
                )
            }
        }

        // Observe pagination state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.paginationState.collect { state ->
                when {
                    state.isLoading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.errorLayout.visibility = View.GONE
                        binding.recyclerView.visibility = View.GONE
                    }
                    state.error != null && viewModel.posts.value.isEmpty() -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerView.visibility = View.GONE
                        binding.errorLayout.visibility = View.VISIBLE
                        binding.tvError.text = state.error
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                        binding.errorLayout.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE

                        // Show snackbar for load more errors
                        if (state.error != null && viewModel.posts.value.isNotEmpty()) {
                            Snackbar.make(
                                binding.root,
                                "Failed to load more posts: ${state.error}",
                                Snackbar.LENGTH_LONG
                            ).setAction("Retry") {
                                viewModel.loadNextPage()
                            }.show()
                            viewModel.clearError()
                        }
                    }
                }

                // Update posts with current loading state
                val posts = viewModel.posts.value
                if (posts.isNotEmpty()) {
                    postsAdapter.submitPostsWithLoading(
                        posts = posts,
                        showLoading = state.isLoadingMore && state.hasMoreData
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun showErrorSnackbar(message: String, actionText: String = "Retry", action: () -> Unit = {}) {
        view?.let { v ->
            com.google.android.material.snackbar.Snackbar.make(v, message, com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                .setAction(actionText) { action() }
                .show()
        }
    }

    private fun handlePaginationError(error: String) {
        // Only show snackbar if we have existing data
        if (viewModel.posts.value.isNotEmpty()) {
            showErrorSnackbar("Failed to load more posts") {
                viewModel.loadNextPage()
            }
        }
    }
}