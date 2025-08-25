package com.example.echoposts.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val email: String,
    val password: String,
    val createdAt: Long = System.currentTimeMillis()
)