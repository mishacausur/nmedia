package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: Flow<List<Post>>
    suspend fun like(postId: Long, isLiked: Boolean): Post
    suspend fun share(postId: Long)
    suspend fun remove(id: Long)
    suspend fun save(post: Post): Post
    suspend fun getAllAsync()
}