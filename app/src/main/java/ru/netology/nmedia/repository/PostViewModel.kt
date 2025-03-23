package ru.netology.nmedia.repository

import androidx.lifecycle.ViewModel

class PostViewModel: ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.get()
    val likes = repository.getLikes()
    val shares = repository.getShares()
    val views = repository.getViews()
    fun like() = repository.like()
    fun share() = repository.share()
}