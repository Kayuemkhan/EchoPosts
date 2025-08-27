package com.example.echoposts.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echoposts.data.repository.PostRepository
import com.example.echoposts.domain.model.PaginationState
import com.example.echoposts.domain.model.Post
import com.example.echoposts.domain.model.SearchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _paginationState = MutableStateFlow(PaginationState())
    val paginationState: StateFlow<PaginationState> = _paginationState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private val _isSearchMode = MutableStateFlow(false)
    val isSearchMode: StateFlow<Boolean> = _isSearchMode.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var currentPage = 0
    private val allPosts = mutableListOf<Post>()
    private var searchJob: Job? = null

    init {
        loadFirstPage()
    }

    private fun loadFirstPage() {
        viewModelScope.launch {
            _paginationState.value = _paginationState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val result = postRepository.getPostsPaginated(0)
                result.fold(
                    onSuccess = { paginatedResponse ->
                        allPosts.clear()
                        allPosts.addAll(paginatedResponse.data)
                        _posts.value = allPosts.toList()
                        currentPage = 1

                        _paginationState.value = _paginationState.value.copy(
                            isLoading = false,
                            hasMoreData = paginatedResponse.hasMore,
                            currentPage = currentPage
                        )
                    },
                    onFailure = { exception ->
                        _paginationState.value = _paginationState.value.copy(
                            isLoading = false,
                            error = exception.message
                        )
                    }
                )
            } catch (e: Exception) {
                _paginationState.value = _paginationState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun loadNextPage() {
        if (_isSearchMode.value) return // Don't paginate in search mode

        val currentState = _paginationState.value
        if (currentState.isLoadingMore || !currentState.hasMoreData) return

        viewModelScope.launch {
            _paginationState.value = currentState.copy(isLoadingMore = true)

            try {
                val result = postRepository.getPostsPaginated(currentPage)
                result.fold(
                    onSuccess = { paginatedResponse ->
                        allPosts.addAll(paginatedResponse.data)
                        _posts.value = allPosts.toList()
                        currentPage++

                        _paginationState.value = _paginationState.value.copy(
                            isLoadingMore = false,
                            hasMoreData = paginatedResponse.hasMore,
                            currentPage = currentPage
                        )
                    },
                    onFailure = { exception ->
                        _paginationState.value = _paginationState.value.copy(
                            isLoadingMore = false,
                            error = exception.message
                        )
                    }
                )
            } catch (e: Exception) {
                _paginationState.value = _paginationState.value.copy(
                    isLoadingMore = false,
                    error = e.message
                )
            }
        }
    }

    fun refreshPosts() {
        if (_isSearchMode.value) {
            // If in search mode, re-execute search
            performSearch(_searchQuery.value)
            return
        }

        currentPage = 0
        allPosts.clear()
        _isRefreshing.value = true

        viewModelScope.launch {
            try {
                val result = postRepository.getPostsPaginated(0, refresh = true)
                result.fold(
                    onSuccess = { paginatedResponse ->
                        allPosts.clear()
                        allPosts.addAll(paginatedResponse.data)
                        _posts.value = allPosts.toList()
                        currentPage = 1

                        _paginationState.value = _paginationState.value.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            hasMoreData = paginatedResponse.hasMore,
                            currentPage = currentPage,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _paginationState.value = _paginationState.value.copy(
                            error = exception.message
                        )
                    }
                )
            } catch (e: Exception) {
                _paginationState.value = _paginationState.value.copy(
                    error = e.message
                )
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query

        searchJob?.cancel()

        if (query.isBlank()) {
            exitSearchMode()
            return
        }

        _isSearchMode.value = true

        searchJob = viewModelScope.launch {
            delay(300) // Debounce search
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            exitSearchMode()
            return
        }

        viewModelScope.launch {
            _searchState.value = SearchState.Searching

            try {
                val result = postRepository.searchPosts(query.trim())
                result.fold(
                    onSuccess = { searchResults ->
                        if (searchResults.isEmpty()) {
                            _searchState.value = SearchState.Empty(query)
                        } else {
                            _searchState.value = SearchState.Results(searchResults, query)
                            _posts.value = searchResults
                        }
                    },
                    onFailure = { exception ->
                        _searchState.value = SearchState.Error(exception.message ?: "Search failed")
                    }
                )
            } catch (e: Exception) {
                _searchState.value = SearchState.Error(e.message ?: "Search failed")
            }
        }
    }

    fun exitSearchMode() {
        _isSearchMode.value = false
        _searchState.value = SearchState.Idle
        _searchQuery.value = ""
        _posts.value = allPosts.toList() // Restore original posts
        searchJob?.cancel()
    }

    fun toggleFavourite(post: Post) {
        viewModelScope.launch {
            val newFavouriteStatus = postRepository.toggleFavourite(post.id)

            // Update the current list with the new favourite status
            if (_isSearchMode.value) {
                // Update search results
                val currentSearchState = _searchState.value
                if (currentSearchState is SearchState.Results) {
                    val updatedPosts = currentSearchState.posts.map {
                        if (it.id == post.id) {
                            it.copy(isFavourite = newFavouriteStatus)
                        } else {
                            it
                        }
                    }
                    _searchState.value = SearchState.Results(updatedPosts, currentSearchState.query)
                    _posts.value = updatedPosts
                }
            } else {
                // Update main posts
                val updatedPosts = allPosts.map {
                    if (it.id == post.id) {
                        it.copy(isFavourite = newFavouriteStatus)
                    } else {
                        it
                    }
                }
                allPosts.clear()
                allPosts.addAll(updatedPosts)
                _posts.value = allPosts.toList()
            }
        }
    }

    fun clearError() {
        _paginationState.value = _paginationState.value.copy(error = null)
        if (_searchState.value is SearchState.Error) {
            _searchState.value = SearchState.Idle
        }
    }
}
