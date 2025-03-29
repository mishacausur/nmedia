package ru.netology.nmedia.repository

import androidx.lifecycle.ViewModel

class PostViewModel: ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()
    fun like(postId: UInt) = repository.like(postId)
    fun share(postId: UInt) = repository.share(postId)
}