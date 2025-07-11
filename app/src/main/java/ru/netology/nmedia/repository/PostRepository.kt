package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: LiveData<List<Post>>
    suspend fun like(postId: Long, isLiked: Boolean): Post
    suspend fun share(postId: Long)
    suspend fun remove(id: Long)
    suspend fun save(post: Post): Post
    suspend fun getAllAsync()
    suspend fun  likeLocally(postId: Long)
    suspend fun likeRemotely(postId: Long)
    suspend fun undoLike(postId: Long)
    suspend fun removeLocally(postId: Long)
}