package ru.netology.nmedia.repository

import androidx.lifecycle.map
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.fromDto
import ru.netology.nmedia.entity.toDTO

class PostRepositoryNetworkImpl(private val dao: PostDao) : PostRepository {

    override val data = dao.getAll().map {
        it.toDTO()
    }

    override suspend fun like(postId: Long, isLiked: Boolean): Post {
        dao.like(postId)
        try {
            val response = if (!isLiked) {
                ApiService.service.like(postId)
            } else {
                ApiService.service.unlike(postId)
            }
            dao.like(postId)
            return response
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun share(postId: Long) {
        try {
            println(
                """
                val request: Request = Request.Builder()
                .post("".toRequestBody())
                .url("BASE_URL/api/slow/posts/$postId/shares")
                .build()

            client.newCall(request)
                .execute()
                .close()
            """.trimIndent()
            )
            dao.share(postId)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun removeLocally(postId: Long) {
        dao.remove(postId)
    }

    override suspend fun remove(id: Long) {
        removeLocally(id)
        try {
            ApiService.service.remove(id)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun save(post: Post): Post {
        try {
            val response = ApiService.service.save(post)
            dao.insert(PostEntity.fromDTO(response))
            return response
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAllAsync() {
        try {
            val posts = ApiService.service.getAll()
            posts.fromDto().forEach {
                dao.insert(it)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun likeLocally(postId: Long) {
        val post = dao.getById(postId) ?: return
        val liked = !post.isLiked
        dao.insert(post.copy(
            isLiked = liked,
            likes = if (liked) post.likes + 1 else post.likes - 1
        ))
    }

    override suspend fun likeRemotely(postId: Long) {
        val post = dao.getById(postId) ?: return
        if (post.isLiked) {
            ApiService.service.like(postId)
        } else {
            ApiService.service.unlike(postId)
        }
    }

    override suspend fun undoLike(postId: Long) {
        val post = dao.getById(postId) ?: return
        val liked = !post.isLiked
        dao.insert(post.copy(
            isLiked = liked,
            likes = if (liked) post.likes + 1 else post.likes - 1
        ))
    }
}