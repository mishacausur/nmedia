package ru.netology.nmedia.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post

private val empty = Post(
    id = 0u,
    author = "",
    content = "",
    published = "",
    isLiked = false,
    shares = 0u,
    views = 0u,
    likes = 0u,
)

class PostViewModel: ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()
    val edited = MutableLiveData(empty)
    fun like(postId: UInt) = repository.like(postId)
    fun share(postId: UInt) = repository.share(postId)
    fun remove(postId: UInt) = repository.remove(postId)

    fun save(text: String) {
        edited.value?.let {
            if (it.content != text) {
                repository.save(it.copy(content = text))
            }
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun cancel() {
        edited.value = empty
    }
}