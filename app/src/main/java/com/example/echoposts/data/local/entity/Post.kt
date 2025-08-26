package com.example.echoposts.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
    val isFavourite: Boolean = false,
    val cachedAt: Long = System.currentTimeMillis()
)