package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: Flow<PagingData<Post>>
    fun newerCount(id: Long): Flow<Int>
    suspend fun like(postId: Long)
    suspend fun share(postId: Long)
    suspend fun remove(id: Long)
    suspend fun save(post: Post): Post
    suspend fun getAllAsync()
    suspend fun undoLike(postId: Long)
    suspend fun removeLocally(postId: Long)
    suspend fun setAllVisible()
    fun getNewerLocalCount(): Flow<Int>
    suspend fun getPostById(postId: Long): Post?
    suspend fun clearAll()
}