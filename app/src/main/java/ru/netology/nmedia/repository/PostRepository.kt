package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun like(postId: Long)
    fun share(postId: Long)
    fun remove(postId: Long)
    fun save(post: Post)
}