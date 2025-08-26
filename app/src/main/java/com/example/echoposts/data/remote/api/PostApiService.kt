package com.example.echoposts.data.remote.api

import com.example.echoposts.data.remote.dto.PostDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PostApiService {
    @GET("posts")
    suspend fun getAllPosts(): Response<List<PostDto>>
    
    @GET("posts")
    suspend fun getPostsPaginated(
        @Query("_start") start: Int,
        @Query("_limit") limit: Int
    ): Response<List<PostDto>>
}