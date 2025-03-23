package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Counter
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl: PostRepository {

    private var post = Post(
        id = 1u,
        author = "Нетология. Университет интернет-профессий будущего",
        published = "21 мая в 18:36",
        content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        isLiked = false
    )


    private val data = MutableLiveData(post)
    private var _shares: UInt = 0u
    private var _views: UInt = 710_000u
    private var _liked: UInt = 1_999u
    private val liked = MutableLiveData(_liked)
    private val shares = MutableLiveData(_shares)
    private val views = MutableLiveData(_views)
    override fun get(): LiveData<Post> = data
    override fun getLikes(): LiveData<UInt> = liked
    override fun getShares(): LiveData<UInt> = shares
    override fun getViews(): LiveData<UInt> = views

    override fun like() {
        post = post.copy(isLiked = !post.isLiked)
        data.value = post

        if (post.isLiked) {
            _liked++
        } else {
            _liked--
        }
        liked.value = _liked
    }

    override fun share() {
        _shares++
        shares.value = _shares
    }
}