package com.example.echoposts.domain.model

sealed class SearchState {
    object Idle : SearchState()
    object Searching : SearchState()
    data class Results(val posts: List<Post>, val query: String) : SearchState()
    data class Error(val message: String) : SearchState()
    data class Empty(val query: String) : SearchState()
}