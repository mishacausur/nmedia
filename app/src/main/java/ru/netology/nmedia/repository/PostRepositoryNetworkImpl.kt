package ru.netology.nmedia.repository

import androidx.lifecycle.map
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.fromDto
import ru.netology.nmedia.entity.toDTO

class PostRepositoryNetworkImpl(private val dao: PostDao) : PostRepository {

    override val data = dao.getAll().map {
        it.toDTO()
    }

    override suspend fun like(postId: Long, isLiked: Boolean): Post {
        return if (!isLiked) {
            ApiService.service.like(postId)
        } else {
            ApiService.service.unlike(postId)
        }
    }

    override suspend fun share(postId: Long) {
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
    }

    override suspend fun remove(id: Long) {
        ApiService.service.remove(id)
    }

    override suspend fun save(post: Post): Post {
        return ApiService.service.save(post)
    }

    override suspend fun getAllAsync() {
        val posts = ApiService.service.getAll()
        posts.fromDto().forEach {
            dao.insert(it)
        }

    }

}