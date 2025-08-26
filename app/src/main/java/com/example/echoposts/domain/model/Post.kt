package com.example.echoposts.domain.model

data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
    val isFavourite: Boolean = false
)
