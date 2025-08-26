package com.example.echoposts.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echoposts.data.repository.PostRepository
import com.example.echoposts.domain.model.PaginationState
import com.example.echoposts.domain.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private var currentPage = 0
    private val allPosts = mutableListOf<Post>()

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

    fun toggleFavourite(post: Post) {
        viewModelScope.launch {
            val newFavouriteStatus = postRepository.toggleFavourite(post.id)

            // Update the current list with the new favourite status
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

    fun clearError() {
        _paginationState.value = _paginationState.value.copy(error = null)
    }
}
