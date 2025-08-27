package com.example.echoposts.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echoposts.data.repository.PostRepository
import com.example.echoposts.domain.model.Post
import com.example.echoposts.domain.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _favouritePostsState = MutableStateFlow<UiState<List<Post>>>(UiState.Loading)
    val favouritePostsState: StateFlow<UiState<List<Post>>> = _favouritePostsState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _deletingPostIds = MutableStateFlow<Set<Int>>(emptySet())
    val deletingPostIds: StateFlow<Set<Int>> = _deletingPostIds.asStateFlow()

    init {
        loadFavouritePosts()
    }

    fun loadFavouritePosts() {
        viewModelScope.launch {
            _favouritePostsState.value = UiState.Loading

            try {
                postRepository.getFavouritePosts().collect { favouritePosts ->
                    if (favouritePosts.isEmpty()) {
                        _favouritePostsState.value = UiState.Empty
                    } else {
                        _favouritePostsState.value = UiState.Success(favouritePosts)
                    }
                }
            } catch (e: Exception) {
                _favouritePostsState.value = UiState.Error(e.message ?: "Failed to load favourites")
            }
        }
    }

    fun refreshFavourites() {
        _isRefreshing.value = true

        viewModelScope.launch {
            try {
                postRepository.getFavouritePosts()
                    .catch { e ->
                        _favouritePostsState.value = UiState.Error(e.message ?: "Failed to refresh favourites")
                    }
                    .collect { favouritePosts ->
                        if (favouritePosts.isEmpty()) {
                            _favouritePostsState.value = UiState.Empty
                        } else {
                            _favouritePostsState.value = UiState.Success(favouritePosts)
                        }
                        _isRefreshing.value = false
                    }
            } catch (e: Exception) {
                _favouritePostsState.value = UiState.Error(e.message ?: "Failed to refresh favourites")
                _isRefreshing.value = false
            }
        }
    }

    fun removeFromFavourites(post: Post) {
        viewModelScope.launch {
            // Add to deleting set for UI feedback
            _deletingPostIds.value = _deletingPostIds.value + post.id

            try {
                val success = postRepository.toggleFavourite(post.id)
                if (success) {
                    // Remove from current list immediately for better UX
                    val currentState = _favouritePostsState.value
                    if (currentState is UiState.Success) {
                        val updatedList = currentState.data.filter { it.id != post.id }
                        _favouritePostsState.value = if (updatedList.isEmpty()) {
                            UiState.Empty
                        } else {
                            UiState.Success(updatedList)
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle error if needed
                _favouritePostsState.value = UiState.Error("Failed to remove from favourites")
            } finally {
                // Remove from deleting set
                _deletingPostIds.value = _deletingPostIds.value - post.id
            }
        }
    }

    fun clearAllFavourites() {
        viewModelScope.launch {
            val currentState = _favouritePostsState.value
            if (currentState is UiState.Success) {
                _favouritePostsState.value = UiState.Loading

                try {
                    // Remove all favourites
                    currentState.data.forEach { post ->
                        postRepository.toggleFavourite(post.id)
                    }
                    _favouritePostsState.value = UiState.Empty
                } catch (e: Exception) {
                    _favouritePostsState.value = UiState.Error("Failed to clear favourites")
                }
            }
        }
    }

    fun retryLoading() {
        loadFavouritePosts()
    }
}