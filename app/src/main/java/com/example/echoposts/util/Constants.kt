package com.example.echoposts.util

object Constants {
    // API Constants
    const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    const val TOTAL_POSTS_JSONPLACEHOLDER = 100

    // Pagination Constants
    const val DEFAULT_PAGE_SIZE = 10
    const val FIRST_PAGE = 0
    const val PAGINATION_THRESHOLD = 2

    // Database Constants
    const val DATABASE_NAME = "echo_posts_database"
    const val DATABASE_VERSION = 1

    // Validation Constants
    const val MIN_PASSWORD_LENGTH = 6
    const val EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    // Cache Constants
    const val CACHE_TIMEOUT_MINUTES = 15
    const val MAX_CACHE_SIZE_MB = 50
}