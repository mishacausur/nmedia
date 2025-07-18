package ru.netology.nmedia.repository

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.FeedModel
import ru.netology.nmedia.entity.FeedModelState
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.utils.SingleLiveEvent

class HttpException(val code: Int) : Throwable("HTTP error with code $code")

private val empty = Post(
    id = 0,
    author = "Me",
    authorAvatar = "",
    content = "",
    published = "",
    isLiked = false,
    shares = 0u,
    views = 0u,
    likes = 0,
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryNetworkImpl(
        dao = AppDb.getInstance(application).postDao
    )
    private var draft: String? = null
    val data: LiveData<FeedModel> = repository.data.map {
        FeedModel(
            posts = it,
            empty = it.isEmpty(),
        )
    }
    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _errorMessage = SingleLiveEvent<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        loadPosts()
    }

    fun retryLoad() {
        loadPosts()
    }


    fun loadPosts() {
        _state.value = FeedModelState(loading = true)
        viewModelScope.launch {
            try {
                repository.getAllAsync()
                _state.value = FeedModelState()
            } catch (_: Exception) {
                _state.value = FeedModelState(error = true)
            }

        }
    }

    fun like(postId: Long) {
        viewModelScope.launch {
            try {
                repository.like(postId)
            } catch (e: Exception) {
                _errorMessage.postValue("Error occured: ${e.message}")
            }
        }
    }

    fun share(postId: Long) {
        viewModelScope.launch {
            repository.share(postId)
        }
    }

    fun remove(postId: Long) {
        viewModelScope.launch {
            try {
                repository.removeLocally(postId)
                repository.remove(postId)
            } catch (e: Exception) {
                _errorMessage.postValue("Error occurred: ${e.message}")
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            try {
                edited.value?.let {
                    val updatedPost = it
                    repository.save(updatedPost)
                }
                edited.value = empty
            } catch (e: Exception) {
                _errorMessage.postValue("Error occured: ${e.message}")
            }
        }
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