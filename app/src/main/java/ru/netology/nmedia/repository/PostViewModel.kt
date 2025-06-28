package ru.netology.nmedia.repository

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.utils.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

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

    private val _errorMessage = SingleLiveEvent<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        loadPosts()
    }

    fun retryLoad() {
        loadPosts()
    }


    fun loadPosts() {
        _data.postValue(FeedModel(loading = true))

        repository.getAllAsync(object : PostRepository.GetAllCallback<List<Post>> {
            override fun onSuccess(result: List<Post>) {
                _data.postValue(FeedModel(posts = result, empty = result.isEmpty()))
            }

            override fun onError(e: Throwable) {
                _errorMessage.postValue("Ошибка загрузки: ${e.message}")
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun like(postId: Long) {

        val post = _data.value?.posts?.find { it.id == postId } ?: return
        val updatedPost = post.copy(
            isLiked = !post.isLiked,
            likes = if (post.isLiked) post.likes - 1 else post.likes + 1,
            author = post.author,
            authorAvatar = post.authorAvatar,
            published = post.published,
            content = post.content,
            shares = post.shares,
            views = post.views
        )
        _data.value?.posts?.map { if (it.id == postId) updatedPost else it }?.let {
            _data.postValue(_data.value?.copy(posts = it))
        }

        repository.like(postId, post.isLiked, object : PostRepository.GetAllCallback<Post> {
            override fun onSuccess(result: Post) {

                _data.value?.posts?.let { posts ->
                    val updatedPosts = posts.map {
                        if (it.id == postId) {
                            result.copy(published = it.published)
                        } else it
                    }
                    _data.postValue(_data.value?.copy(posts = updatedPosts))
                }
            }

            override fun onError(e: Throwable) {
                _errorMessage.postValue("Ошибка при лайке: ${e.message}")
                _data.value?.posts?.let { posts ->
                    val originalPost = post.copy(
                        isLiked = post.isLiked,
                        likes = post.likes,
                        author = post.author,
                        authorAvatar = post.authorAvatar,
                        published = post.published,
                        content = post.content,
                        shares = post.shares,
                        views = post.views
                    )
                    val updatedPosts = posts.map {
                        if (it.id == postId) originalPost else it
                    }
                    _data.postValue(_data.value?.copy(posts = updatedPosts))
                }
            }
        })
    }
    fun share(postId: Long) = repository.share(postId)
    fun remove(postId: Long) {
        val oldPosts = _data.value?.posts.orEmpty()
        _data.postValue(
            _data.value?.copy(
                posts = _data.value?.posts.orEmpty().filter { it.id != postId })
        )
        repository.remove(postId, object : PostRepository.GetAllCallback<Unit> {
            override fun onSuccess(result: Unit) {}

            override fun onError(e: Throwable) {
                _errorMessage.postValue("Ошибка при удалении: ${e.message}")
                _data.postValue(_data.value?.copy(posts = oldPosts))
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        edited.value?.let {
            val updatedPost = it

            repository.save(updatedPost, object : PostRepository.GetAllCallback<Post> {
                override fun onSuccess(result: Post) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Throwable) {
                    _errorMessage.postValue("Ошибка при сохранении: ${e.message}")
                    _data.value = FeedModel(error = true)
                }
            })
        }
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