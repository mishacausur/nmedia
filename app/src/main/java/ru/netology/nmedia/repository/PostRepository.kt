package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun like(postId: Long, callback: GetAllCallback<Post>)
    fun share(postId: Long)
    fun remove(id: Long, callback: GetAllCallback<Unit>)
    fun save(post: Post, callback: GetAllCallback<Post>)
    fun getAllAsync(callback: GetAllCallback<List<Post>>)

    interface GetAllCallback<T>  {
        fun onSuccess(result: T) {}
        fun onError(e: Exception) {}
    }
}