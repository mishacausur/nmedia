package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

class PostRepositoryRoomImpl(private val dao: PostDao) : PostRepository {
    private val data = dao.getAll()
    override fun getAll(): LiveData<List<Post>> = data.map { list -> list.map(PostEntity::toDTO) }
    override fun like(postId: Long) = dao.like(postId)
    override fun share(postId: Long) = dao.share(postId)
    override fun remove(postId: Long) = dao.remove(postId)
    override fun save(post: Post) = dao.save(PostEntity.fromDTO(post))
}