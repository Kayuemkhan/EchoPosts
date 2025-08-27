package com.example.echoposts.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import com.example.echoposts.data.local.entity.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<Post>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

    @Query("SELECT * FROM posts ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun getPostsPaginated(limit: Int, offset: Int): List<Post>

    @Query("SELECT * FROM posts WHERE title LIKE :query OR body LIKE :query ORDER BY id ASC")
    suspend fun searchPosts(query: String): List<Post>

    @Query("SELECT * FROM posts WHERE isFavourite = 1 ORDER BY id ASC")
    fun getFavouritePosts(): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE isFavourite = 1 ORDER BY id ASC")
    suspend fun getFavouritePostsList(): List<Post>

    @Update
    suspend fun updatePost(post: Post)

    @Query("UPDATE posts SET isFavourite = :isFavourite WHERE id = :postId")
    suspend fun updateFavouriteStatus(postId: Int, isFavourite: Boolean)

    @Query("SELECT COUNT(*) FROM posts")
    suspend fun getPostCount(): Int

    @Query("SELECT * FROM posts WHERE id = :postId LIMIT 1")
    suspend fun getPostById(postId: Int): Post?

    @Query("SELECT * FROM posts ORDER BY id ASC")
    suspend fun getAllPosts(): List<Post>

    @Query("DELETE FROM posts")
    suspend fun clearAllPosts()

    @Query("DELETE FROM posts WHERE id IN (:postIds)")
    suspend fun deletePosts(postIds: List<Int>)

    @Query("SELECT MAX(id) FROM posts")
    suspend fun getMaxPostId(): Int?

    @Query("SELECT COUNT(*) FROM posts WHERE id >= :startId AND id < :endId")
    suspend fun getPostCountInRange(startId: Int, endId: Int): Int

    @Transaction
    suspend fun insertPostsForPage(posts: List<Post>, page: Int, pageSize: Int) {
        insertPosts(posts)
    }
}