package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): LiveData<List<Post>>
    fun like(postId: UInt)
    fun share(postId: UInt)
    fun remove(postId: UInt)
    fun save(post: Post)
}