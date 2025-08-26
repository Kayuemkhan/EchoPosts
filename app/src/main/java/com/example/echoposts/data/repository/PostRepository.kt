package com.example.echoposts.data.repository
import com.example.echoposts.data.local.dao.PostDao
import com.example.echoposts.data.mapper.toDomainModel
import com.example.echoposts.data.mapper.toEntity
import com.example.echoposts.data.remote.api.PostApiService
import com.example.echoposts.domain.model.PaginatedResponse
import com.example.echoposts.domain.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val apiService: PostApiService,
    private val postDao: PostDao
) {

    companion object {
        const val PAGE_SIZE = 10
        const val TOTAL_POSTS = 100 // JSONPlaceholder has 100 posts
    }

    suspend fun getPostsPaginated(page: Int, refresh: Boolean = false): Result<PaginatedResponse<Post>> {
        return withContext(Dispatchers.IO) {
            try {
                if (refresh && page == 0) {

                }

                val start = page * PAGE_SIZE
                val hasMore = start + PAGE_SIZE < TOTAL_POSTS

                val response = apiService.getPostsPaginated(start, PAGE_SIZE)

                if (response.isSuccessful && response.body() != null) {
                    val posts = response.body()!!

                    if (posts.isNotEmpty()) {
                        postDao.insertPosts(posts.map { it.toEntity() })
                    }

                    val paginatedResponse = PaginatedResponse(
                        data = posts.map { it.toDomainModel() },
                        hasMore = hasMore,
                        totalCount = TOTAL_POSTS
                    )

                    Result.success(paginatedResponse)
                } else {
                    // Try to get cached data if API fails
                    val cachedPosts = postDao.getPostsPaginated(PAGE_SIZE, start)
                    if (cachedPosts.isNotEmpty()) {
                        val paginatedResponse = PaginatedResponse(
                            data = cachedPosts.map { it.toDomainModel() },
                            hasMore = start + PAGE_SIZE < postDao.getPostCount(),
                            totalCount = postDao.getPostCount()
                        )
                        Result.success(paginatedResponse)
                    } else {
                        Result.failure(Exception("Failed to load posts: ${response.message()}"))
                    }
                }
            } catch (e: Exception) {

                try {
                    val start = page * PAGE_SIZE
                    val cachedPosts = postDao.getPostsPaginated(PAGE_SIZE, start)
                    if (cachedPosts.isNotEmpty()) {
                        val paginatedResponse = PaginatedResponse(
                            data = cachedPosts.map { it.toDomainModel() },
                            hasMore = start + PAGE_SIZE < postDao.getPostCount(),
                            totalCount = postDao.getPostCount()
                        )
                        Result.success(paginatedResponse)
                    } else {
                        Result.failure(e)
                    }
                } catch (cacheException: Exception) {
                    Result.failure(e)
                }
            }
        }
    }

    suspend fun toggleFavourite(postId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val allPosts = postDao.getPostsPaginated(1000, 0) // Get all cached posts
                val currentPost = allPosts.find { it.id == postId }
                currentPost?.let {
                    val newStatus = !it.isFavourite
                    postDao.updateFavouriteStatus(postId, newStatus)
                    newStatus
                } ?: false
            } catch (e: Exception) {
                false
            }
        }
    }

    suspend fun getFavouritePosts(): Flow<List<Post>> = flow {
        postDao.getFavouritePosts().collect { favourites ->
            emit(favourites.map { it.toDomainModel() })
        }
    }.flowOn(Dispatchers.IO)
}