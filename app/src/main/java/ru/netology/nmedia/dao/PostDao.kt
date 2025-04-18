package ru.netology.nmedia.dao

import ru.netology.nmedia.dto.Post

interface PostDao {
    fun getAll(): List<Post>
    fun like(postId: Long)
    fun share(postId: Long)
    fun remove(postId: Long)
    fun save(post: Post): Post
}