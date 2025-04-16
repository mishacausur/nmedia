package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post

class PostRepositorySQLiteImpl(
    private val dao: PostDao
) : PostRepository {
    private var posts = emptyList<Post>()
        set(value) {
            field = value
            data.value = value
        }
    private val data = MutableLiveData(posts)

    init {
        posts = dao.getAll()
        data.value = posts
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun like(postId: Long) {
        dao.like(postId)
        posts = posts.map {
            if (it.id != postId) it else it.copy(
                isLiked = !it.isLiked,
                likes = if (!it.isLiked) it.likes + 1u else it.likes - 1u
            )
        }
    }

    override fun share(postId: Long) {
        dao.share(postId)
        posts = posts.map {
            if (it.id != postId) it else it.copy(shares = it.shares + 1u)
        }
    }

    override fun remove(postId: Long) {
        dao.remove(postId)
        posts = posts.filter { it.id != postId }
    }

    override fun save(post: Post) {
        val id = post.id
        val saved = dao.save(post)
        posts = if (id == 0L) {
            listOf(saved) + posts
        } else {
            posts.map {
                if (it.id != id) it else saved
            }
        }
    }
}