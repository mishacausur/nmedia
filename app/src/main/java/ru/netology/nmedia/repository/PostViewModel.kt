package ru.netology.nmedia.repository

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.utils.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    author = "",
    content = "",
    published = "",
    isLiked = false,
    shares = 0u,
    views = 0u,
    likes = 0u,
)

class PostViewModel(application: Application): AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryNetworkImpl()
    private var draft: String? = null
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        thread {
            // Начинаем загрузку
            _data.postValue(FeedModel(loading = true))
            try {
                // Данные успешно получены
                val posts = repository.getAll()
                FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (e: IOException) {
                // Получена ошибка
                FeedModel(error = true)
            }.also(_data::postValue)
        }
    }

    fun like(postId: Long) {
        thread { repository.like(postId) }
    }
    fun share(postId: Long) = repository.share(postId)
    fun remove(postId: Long) {
        thread {
            // Оптимистичная модель
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != postId }
                )
            )
            try {
                repository.remove(postId)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }

    fun save() {
        edited.value?.let {
            thread {
                repository.save(it)
                _postCreated.postValue(Unit)
            }
        }
        draft = null
        edited.value = empty
    }

    fun tempSave(text: String) {
        draft = text
    }

    fun getDraft(): String? = draft

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun cancel() {
        edited.value = empty
    }
}